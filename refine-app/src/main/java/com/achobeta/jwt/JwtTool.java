package com.achobeta.jwt;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.achobeta.domain.IRedisService;
import com.achobeta.types.exception.UnauthorizedException;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class JwtTool {

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private IRedisService redis;

    private final JWTSigner jwtSigner;

    // 初始化：加载密钥对并创建签名器
    public JwtTool(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner(jwtProperties.getAlgorithm(), keyPair);
    }

    /**
     * 生成access-token（短期访问令牌）
     * 携带用户ID和权限相关信息
     */
    public String createAccessToken(Long userId, Map<String, Object> extraClaims) {
        // 基础载荷：必含用户ID
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "access"); // 标记Token类型
        // 添加额外载荷（如角色、权限）
        if (extraClaims != null) {
            claims.putAll(extraClaims);
        }
        // 生成Token
        return createToken(claims, jwtProperties.getAccessToken().getTtl());
    }

    /**
     * 生成refresh-token（长期刷新令牌）
     * 仅携带用户ID和唯一标识（用于注销）
     */
    public String createRefreshToken(Long userId) {
        String jti = UUID.randomUUID().toString(); // 唯一标识，用于注销
        // 载荷：用户ID + 唯一标识 + 类型
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("jti", jti);
        claims.put("type", "refresh"); // 标记Token类型
        // 生成Token
        String refreshToken = createToken(claims, jwtProperties.getRefreshToken().getTtl());
        // 存入Redis：key=refresh:{jti}, value=userId，过期时间同Token
        redis.setValue(
                "refresh:" + jti,
                userId.toString(),
                jwtProperties.getRefreshToken().getTtl().toMillis()
        );
        return refreshToken;
    }

    /**
     * 通用Token生成方法
     */
    private String createToken(Map<String, Object> claims, Duration ttl) {
        return JWT.create()
                .addPayloads(claims) // 设置载荷
                .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis())) // 过期时间
                .setSigner(jwtSigner) // 签名器
                .sign(); // 生成签名
    }

    /**
     * 解析access-token并验证
     * @return 解析后的用户ID
     */
    public Long parseAccessToken(String token) {
        TokenPayload payload = parseToken(token, "access");
        return payload.getUserId();
    }

    /**
     * 解析refresh-token并验证（包含Redis状态校验）
     * @return 解析后的用户ID
     */
    public Long parseRefreshToken(String token) {
        TokenPayload payload = parseToken(token, "refresh");
        // 校验Redis中是否存在该refresh-token（防止已注销）
        String jti = payload.getJti();
        String redisKey = "refresh:" + jti;
        String userIdStr = redis.getValue(redisKey);
        if (StrUtil.isBlank(userIdStr) || !userIdStr.equals(payload.getUserId().toString())) {
            throw new UnauthorizedException("refresh-token已失效，请重新登录");
        }
        return payload.getUserId();
    }

    /**
     * 通用Token解析方法（校验签名、过期时间、类型）
     */
    private TokenPayload parseToken(String token, String expectedType) {
        // 1. 校验非空
        if (StrUtil.isBlank(token)) {
            throw new UnauthorizedException("未登录：token为空");
        }

        JWT jwt;
        try {
            // 2. 解析Token
            jwt = JWT.of(token).setSigner(jwtSigner);
            // 3. 校验签名和过期时间（Hutool自动处理）
            JWTValidator.of(jwt).validateDate().validateDate();
        } catch (ValidateException e) {
            // 细分异常：过期/签名无效
            if (e.getMessage().contains("expired")) {
                throw new UnauthorizedException(expectedType + "-token已过期", e);
            } else {
                throw new UnauthorizedException(expectedType + "-token签名无效", e);
            }
        } catch (Exception e) {
            throw new UnauthorizedException("无效的" + expectedType + "-token：格式错误", e);
        }

        // 4. 校验Token类型
        String actualType = Convert.toStr(jwt.getPayload("type"));
        if (!StrUtil.equals(actualType, expectedType)) {
            throw new UnauthorizedException("无效的" + expectedType + "-token：类型错误");
        }

        // 5. 提取用户ID
        Long userId = Convert.toLong(jwt.getPayload("userId"));
        if (userId == null) {
            throw new UnauthorizedException("无效的" + expectedType + "-token：缺失用户ID");
        }

        // 6. 如果是refresh-token，提取jti
        String jti = Convert.toStr(jwt.getPayload("jti"));
        if (StrUtil.equals(expectedType, "refresh") && StrUtil.isBlank(jti)) {
            throw new UnauthorizedException("无效的refresh-token：缺失唯一标识");
        }

        return new TokenPayload(userId, jti);
    }

    /**
     * 刷新access-token（通过valid的refresh-token）
     * @param refreshToken 有效的refresh-token
     * @return 新的access-token
     */
    public String refreshAccessToken(String refreshToken, Map<String, Object> extraClaims) {
        Long userId = parseRefreshToken(refreshToken); // 先验证refresh-token有效性
        return createAccessToken(userId, extraClaims); // 生成新的access-token
    }

    /**
     * 注销（使refresh-token失效）,退出登录时调用
     * @param refreshToken 待注销的refresh-token
     */
    public void invalidateRefreshToken(String refreshToken) {
        try {
            // 解析refresh-token获取jti
            JWT jwt = JWT.of(refreshToken).setSigner(jwtSigner);
            String jti = Convert.toStr(jwt.getPayload("jti"));
            if (StrUtil.isNotBlank(jti)) {
                // 从Redis删除
                redis.remove("refresh:" + jti);
            }
        } catch (Exception e) {
            // 解析失败的Token直接视为无效，无需处理
        }
    }


    /**
     * 内部类：封装解析后的Token载荷
     */
    @Data
    @AllArgsConstructor
    private static class TokenPayload {
        private final Long userId;
        private final String jti;

    }
}