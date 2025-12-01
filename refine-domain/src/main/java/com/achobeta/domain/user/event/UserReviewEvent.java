package com.achobeta.domain.user.event;

/**
 * @Auth : Malog
 * @Desc : 用户复习错题事件
 * @Time : 2025/11/28
 */
public class UserReviewEvent extends UserBehaviorEvent {
    
    public UserReviewEvent(Object source, String userId, String questionId, 
                          String questionContent, String subject, Integer knowledgePointId) {
        super(source, userId, "review", questionId, questionContent, subject, knowledgePointId);
    }
}