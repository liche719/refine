package com.achobeta.domain.user.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @Auth : Malog
 * @Desc : 用户行为事件基类
 * @Time : 2025/11/28
 */
@Getter
public abstract class UserBehaviorEvent extends ApplicationEvent {
    
    private final String userId;
    private final String actionType;
    private final String questionId;
    private final String questionContent;
    private final String subject;
    private final Integer knowledgePointId;
    
    public UserBehaviorEvent(Object source, String userId, String actionType, 
                           String questionId, String questionContent, 
                           String subject, Integer knowledgePointId) {
        super(source);
        this.userId = userId;
        this.actionType = actionType;
        this.questionId = questionId;
        this.questionContent = questionContent;
        this.subject = subject;
        this.knowledgePointId = knowledgePointId;
    }
}