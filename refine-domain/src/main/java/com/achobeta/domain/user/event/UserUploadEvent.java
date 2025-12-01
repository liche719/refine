package com.achobeta.domain.user.event;

/**
 * @Auth : Malog
 * @Desc : 用户上传题目事件
 * @Time : 2025/11/28
 */
public class UserUploadEvent extends UserBehaviorEvent {
    
    public UserUploadEvent(Object source, String userId, String questionId, 
                          String questionContent, String subject, Integer knowledgePointId) {
        super(source, userId, "upload", questionId, questionContent, subject, knowledgePointId);
    }
}