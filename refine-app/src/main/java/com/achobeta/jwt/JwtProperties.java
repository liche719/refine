package com.achobeta.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * 签名算法（默认RSA256）
     */
    private String algorithm = "rs256";

    /**
     * 私钥路径（用于签名）
     */
    private Resource location;

    /**
     * 密钥别名（用于签名）
     */
    private String alias;

    /**
     * 密码（用于签名）
     */
    private String password;

    /**
     * access-token配置
     */
    private TokenConfig accessToken = new TokenConfig(Duration.ofHours(2));

    /**
     * refresh-token配置
     */
    private TokenConfig refreshToken = new TokenConfig(Duration.ofDays(2));

    /**
     * 内部类：单个Token的配置
     */
    @Data
    @AllArgsConstructor
    public static class TokenConfig {
        /**
         * 过期时间
         */
        private Duration ttl;

    }
}