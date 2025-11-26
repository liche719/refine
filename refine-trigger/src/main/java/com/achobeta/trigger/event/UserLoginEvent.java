package com.achobeta.trigger.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @Auth : Malog
 * @Desc : 用户登录事件
 * @Time : 2025/11/25
 */
@Getter
public class UserLoginEvent extends ApplicationEvent {
    
    private final String userId;
    private final String loginTime;
    
    public UserLoginEvent(Object source, String userId, String loginTime) {
        super(source);
        this.userId = userId;
        this.loginTime = loginTime;
    }
}