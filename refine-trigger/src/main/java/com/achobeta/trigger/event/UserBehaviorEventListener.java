package com.achobeta.trigger.event;

import com.achobeta.domain.rag.service.IVectorService;
import com.achobeta.domain.user.event.UserBehaviorEvent;
import com.achobeta.domain.user.event.UserMistakeEvent;
import com.achobeta.domain.user.event.UserReviewEvent;
import com.achobeta.domain.user.event.UserUploadEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Auth : Malog
 * @Desc : 用户行为事件监听器
 * @Time : 2025/11/28
 */
@Slf4j
@Component
public class UserBehaviorEventListener {

    @Autowired
    private IVectorService vectorService;

    /**
     * 处理用户复习事件
     */
    @EventListener
    @Async
    public void handleUserReviewEvent(UserReviewEvent event) {
        try {
            log.info("检测到用户复习事件，开始记录向量数据，userId:{}, questionId:{}", 
                    event.getUserId(), event.getQuestionId());

            boolean success = vectorService.storeLearningVector(
                    event.getUserId(),
                    event.getQuestionId(),
                    event.getQuestionContent(),
                    event.getActionType(),
                    event.getSubject(),
                    event.getKnowledgePointId()
            );

            if (success) {
                log.info("用户复习行为向量记录成功，userId:{}, questionId:{}", 
                        event.getUserId(), event.getQuestionId());
            } else {
                log.warn("用户复习行为向量记录失败，userId:{}, questionId:{}", 
                        event.getUserId(), event.getQuestionId());
            }

        } catch (Exception e) {
            log.error("处理用户复习事件失败，userId:{}, questionId:{}", 
                    event.getUserId(), event.getQuestionId(), e);
        }
    }

    /**
     * 处理用户错题事件
     */
    @EventListener
    @Async
    public void handleUserMistakeEvent(UserMistakeEvent event) {
        try {
            log.info("检测到用户错题事件，开始记录向量数据，userId:{}, questionId:{}", 
                    event.getUserId(), event.getQuestionId());

            boolean success = vectorService.storeLearningVector(
                    event.getUserId(),
                    event.getQuestionId(),
                    event.getQuestionContent(),
                    event.getActionType(),
                    event.getSubject(),
                    event.getKnowledgePointId()
            );

            if (success) {
                log.info("用户错题行为向量记录成功，userId:{}, questionId:{}", 
                        event.getUserId(), event.getQuestionId());
            } else {
                log.warn("用户错题行为向量记录失败，userId:{}, questionId:{}", 
                        event.getUserId(), event.getQuestionId());
            }

        } catch (Exception e) {
            log.error("处理用户错题事件失败，userId:{}, questionId:{}", 
                    event.getUserId(), event.getQuestionId(), e);
        }
    }

    /**
     * 处理用户上传事件
     */
    @EventListener
    @Async
    public void handleUserUploadEvent(UserUploadEvent event) {
        try {
            log.info("检测到用户上传事件，开始记录向量数据，userId:{}, questionId:{}", 
                    event.getUserId(), event.getQuestionId());

            boolean success = vectorService.storeLearningVector(
                    event.getUserId(),
                    event.getQuestionId(),
                    event.getQuestionContent(),
                    event.getActionType(),
                    event.getSubject(),
                    event.getKnowledgePointId()
            );

            if (success) {
                log.info("用户上传行为向量记录成功，userId:{}, questionId:{}", 
                        event.getUserId(), event.getQuestionId());
            } else {
                log.warn("用户上传行为向量记录失败，userId:{}, questionId:{}", 
                        event.getUserId(), event.getQuestionId());
            }

        } catch (Exception e) {
            log.error("处理用户上传事件失败，userId:{}, questionId:{}", 
                    event.getUserId(), event.getQuestionId(), e);
        }
    }

    /**
     * 处理通用用户行为事件
     */
    @EventListener
    @Async
    public void handleUserBehaviorEvent(UserBehaviorEvent event) {
        try {
            // 避免重复处理已经有专门监听器的事件
            if (event instanceof UserReviewEvent || event instanceof UserMistakeEvent || event instanceof UserUploadEvent) {
                return;
            }

            log.info("检测到用户行为事件，开始记录向量数据，userId:{}, actionType:{}, questionId:{}", 
                    event.getUserId(), event.getActionType(), event.getQuestionId());

            boolean success = vectorService.storeLearningVector(
                    event.getUserId(),
                    event.getQuestionId(),
                    event.getQuestionContent(),
                    event.getActionType(),
                    event.getSubject(),
                    event.getKnowledgePointId()
            );

            if (success) {
                log.info("用户行为向量记录成功，userId:{}, actionType:{}, questionId:{}", 
                        event.getUserId(), event.getActionType(), event.getQuestionId());
            } else {
                log.warn("用户行为向量记录失败，userId:{}, actionType:{}, questionId:{}", 
                        event.getUserId(), event.getActionType(), event.getQuestionId());
            }

        } catch (Exception e) {
            log.error("处理用户行为事件失败，userId:{}, actionType:{}, questionId:{}", 
                    event.getUserId(), event.getActionType(), event.getQuestionId(), e);
        }
    }
}