package com.achobeta.domain.user.event;

/**
 * @Auth : Malog
 * @Desc : 用户错题事件
 * @Time : 2025/11/28
 */
public class UserMistakeEvent extends UserBehaviorEvent {
    
    public UserMistakeEvent(Object source, String userId, String questionId, 
                           String questionContent, String subject, Integer knowledgePointId) {
        super(source, userId, "mistake", questionId, questionContent, subject, knowledgePointId);
    }
}