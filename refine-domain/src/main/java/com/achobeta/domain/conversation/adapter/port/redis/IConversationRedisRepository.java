package com.achobeta.domain.conversation.adapter.port.redis;

import com.achobeta.domain.conversation.model.entity.ConversationMessageEntity;

import java.util.List;

/**
 * @Auth : Malog
 * @Desc : Redis会话仓储接口
 * @Time : 2025/11/10
 */
public interface IConversationRedisRepository {

    /**
     * 保存会话历史到Redis
     *
     * @param questionId 错题ID（作为会话ID）
     * @param conversationHistory 会话历史
     * @return 是否保存成功
     */
    boolean saveConversationHistory(String questionId, List<ConversationMessageEntity> conversationHistory);

    /**
     * 从Redis获取会话历史
     *
     * @param questionId 错题ID（作为会话ID）
     * @return 会话历史列表
     */
    List<ConversationMessageEntity> getConversationHistory(String questionId);

    /**
     * 删除Redis中的会话
     *
     * @param questionId 错题ID（作为会话ID）
     * @return 是否删除成功
     */
    boolean deleteConversation(String questionId);

    /**
     * 设置Redis键的过期时间
     *
     * @param questionId 错题ID（作为会话ID）
     * @param expireTime 过期时间（毫秒）
     * @return 是否设置成功
     */
    boolean setExpireTime(String questionId, long expireTime);
}