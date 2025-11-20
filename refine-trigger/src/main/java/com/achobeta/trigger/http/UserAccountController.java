package com.achobeta.trigger.http;

import com.achobeta.api.dto.userAccount.LoginRequestDTO;
import com.achobeta.api.dto.userAccount.RegisterRequestDTO;
import com.achobeta.domain.IRedisService;
import com.achobeta.domain.user.model.valobj.UserLoginVO;
import com.achobeta.domain.user.service.IEmailVerificationService;
import com.achobeta.domain.user.service.IUserAccountService;
import com.achobeta.types.Response;
import com.achobeta.types.annotation.GlobalInterception;
import com.achobeta.types.common.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final IRedisService redis;

    /**
     * 发送邮箱验证码
     *
     * @param userAccount 邮箱
     * @return 发送成功
     */
    @PostMapping("/sendEmailCode")
    public Response sendEmailCode(String userAccount) {
        emailVerificationService.sendEmailCode(userAccount);
        return Response.SYSTEM_SUCCESS();
    }

    @PostMapping("/register")
    public Response register(@RequestBody RegisterRequestDTO request) {
        userAccountService.register(request.getUserAccount(), request.getUserPassword(), request.getUserName(), request.getCheckCode());
        return Response.SYSTEM_SUCCESS();
    }

    @PostMapping("/login")
    public Response<UserLoginVO> login(@RequestBody LoginRequestDTO request) {
        UserLoginVO user = userAccountService.login(request.getUserAccount(), request.getUserPassword());
        return Response.SYSTEM_SUCCESS(user);
    }

    @GlobalInterception
    @PostMapping("/logout")
    public Response logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        redis.remove(Constants.USER_ID_KEY_PREFIX + token);
        return Response.SYSTEM_SUCCESS();
    }

    /**
     * 重置密码（忘记密码）
     */
    @PostMapping("/resetPassword")
    @Validated
    public Response resetPassword(@NotBlank String userEmail, @NotBlank @Pattern(regexp = Constants.REGEX_PASSWORD) String newPassword, @NotBlank String checkCode) {
        userAccountService.resetPassword(userEmail, newPassword, checkCode);
        return Response.SYSTEM_SUCCESS();
    }

    /**
     * 修改密码
     */
    @GlobalInterception
    @PostMapping("/updatePassword")
    public Response updatePassword(HttpServletRequest request, @NotBlank String oldPassword, @NotBlank @Pattern(regexp = Constants.REGEX_PASSWORD) String newPassword) {
        String token = request.getHeader("token");
        String userId = redis.getValue(Constants.USER_ID_KEY_PREFIX + token);
        userAccountService.updatePassword(userId, oldPassword, newPassword);
        return Response.SYSTEM_SUCCESS();
    }

}