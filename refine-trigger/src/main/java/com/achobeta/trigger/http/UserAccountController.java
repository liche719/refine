package com.achobeta.trigger.http;

import com.achobeta.api.dto.LoginRequestDTO;
import com.achobeta.api.dto.RegisterRequestDTO;
import com.achobeta.domain.user.model.valobj.UserLoginVO;
import com.achobeta.domain.user.service.IEmailVerificationService;
import com.achobeta.domain.user.service.IUserAccountService;
import com.achobeta.types.Response;
import com.achobeta.types.annotation.GlobalInterception;
import com.achobeta.types.common.Constants;
import com.achobeta.types.common.UserContext;
import com.achobeta.types.exception.AppException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author liangchaowen
 * @date 2025/10/29
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/userAccount")
@RequiredArgsConstructor
public class UserAccountController {

    private final IEmailVerificationService emailVerificationService;

    private final IUserAccountService userAccountService;


    /**
     * 发送邮箱验证码
     *
     * @param userAccount 邮箱
     * @return 发送成功
     */
    @PostMapping("/sendEmailCode")
    public Response sendEmailCode(@NotBlank(message = "接收验证码邮箱不能为空") String userAccount) {
        try {
            emailVerificationService.sendEmailCode(userAccount);
        } catch (Exception e) {
            throw new AppException(e.getMessage());
        }
        return Response.SYSTEM_SUCCESS();
    }

    @PostMapping("/register")
    public Response register(@RequestBody RegisterRequestDTO request) {
        try {
            userAccountService.register(request.getUserAccount(), request.getUserPassword(), request.getUserName(), request.getCheckCode());
        } catch (Exception e) {
            throw new AppException(e.getMessage());
        }
        return Response.SYSTEM_SUCCESS();
    }

    @PostMapping("/login")
    public Response<UserLoginVO> login(@RequestBody LoginRequestDTO request) {
        UserLoginVO user = null;
        try {
            user = userAccountService.login(request.getUserAccount(), request.getUserPassword());
            log.info("用户 {} 登录", request.getUserAccount());
        } catch (Exception e) {
            throw new AppException(e.getMessage());
        }
        return Response.SYSTEM_SUCCESS(user);
    }

    @GlobalInterception
    @PostMapping("/logout")
    public Response logout(@RequestHeader("refresh-token") String refreshToken) {
        try {
            userAccountService.logout(refreshToken);
            log.info("用户 {} 登出", UserContext.getUserId());
        } catch (Exception e) {
            throw new AppException(e.getMessage());
        }
        return Response.SYSTEM_SUCCESS();
    }

    /**
     * 重置密码（忘记密码）
     */
    @PostMapping("/resetPassword")
    @Validated
    public Response resetPassword(@NotBlank String userAccount, @NotBlank @Pattern(regexp = Constants.REGEX_PASSWORD) String newPassword, @NotBlank String checkCode) {
        try {
            userAccountService.resetPassword(userAccount, newPassword, checkCode);
            log.info("账号 {} 重置密码", userAccount);
        } catch (Exception e) {
            throw new AppException(e.getMessage());
        }
        return Response.SYSTEM_SUCCESS();
    }

    /**
     * 修改密码
     */
    @GlobalInterception
    @PostMapping("/updatePassword")
    public Response updatePassword(@NotBlank String oldPassword, @NotBlank @Pattern(regexp = Constants.REGEX_PASSWORD) String newPassword) {
        String userId = UserContext.getUserId();
        try {
            userAccountService.updatePassword(userId, oldPassword, newPassword);
            log.info("userId {} 修改密码", userId);
        } catch (Exception e) {
            throw new AppException(e.getMessage());
        }
        return Response.SYSTEM_SUCCESS();
    }


    @PostMapping("/refreshToken")
    public Response<Map<String, String>> refreshToken(@RequestHeader("refresh-token") String refreshToken) {
        Map<String, String> newToken = null;
        try {
            newToken = userAccountService.refreshToken(refreshToken);
            log.info("用户刷新access-token");
        } catch (Exception e) {
            throw new AppException(e.getMessage());
        }
        return Response.SYSTEM_SUCCESS(newToken);
    }

}