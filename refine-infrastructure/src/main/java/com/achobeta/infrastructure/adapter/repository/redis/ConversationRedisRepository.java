package com.achobeta.infrastructure.adapter.repository.redis;

import com.achobeta.domain.conversation.adapter.port.redis.IConversationRedisRepository;
import com.achobeta.domain.conversation.model.entity.ConversationMessageEntity;
import com.achobeta.infrastructure.redis.IRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auth : Malog
 * @Desc : Redis会话仓储实现
 * @Time : 2025/11/10
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ConversationRedisRepository implements IConversationRedisRepository {

    private static final String REDIS_CONVERSATION_PREFIX = "conversation:";
    private static final long CONVERSATION_EXPIRE_TIME = 60 * 60 * 1000; // 60分钟过期时间（毫秒）

    private final IRedisService redisService;

    @Override
    public boolean saveConversationHistory(String questionId, List<ConversationMessageEntity> conversationHistory) {
        try {
            String redisKey = REDIS_CONVERSATION_PREFIX + questionId;
            
            // 保存到Redis，设置60分钟过期时间
            redisService.setValue(redisKey, conversationHistory, CONVERSATION_EXPIRE_TIME);

        } catch (Exception e) {
            log.error("保存会话历史到Redis时发生异常: questionId={}", questionId, e);
            return false;
        }
        return true;
    }

    @Override
    public List<ConversationMessageEntity> getConversationHistory(String questionId) {
        try {
            String redisKey = REDIS_CONVERSATION_PREFIX + questionId;
            List<ConversationMessageEntity> history = redisService.getValue(redisKey);
            return history != null ? history : List.of();
        } catch (Exception e) {
            log.error("从Redis获取会话历史失败: questionId={}", questionId, e);
            return List.of();
        }
    }

    @Override
    public boolean deleteConversation(String questionId) {
        try {
            String redisKey = REDIS_CONVERSATION_PREFIX + questionId;
            redisService.remove(redisKey);
        } catch (Exception e) {
            log.error("删除Redis会话时发生异常: questionId={}", questionId, e);
            return false;
        }
        return true;
    }

    @Override
    public boolean setExpireTime(String questionId, long expireTime) {
//        try {
//            String redisKey = REDIS_CONVERSATION_PREFIX + questionId;
//            boolean success = redisService.expire(redisKey, expireTime);
//
//            if (success) {
//                log.debug("设置Redis会话过期时间成功: questionId={}, expireTime={}ms", questionId, expireTime);
//            } else {
//                log.error("设置Redis会话过期时间失败: questionId={}", questionId);
//            }
//
//            return success;
//        } catch (Exception e) {
//            log.error("设置Redis会话过期时间时发生异常: questionId={}", questionId, e);
//            return false;
//        }
        // TODO 设置过期时间
        return true;
    }
}