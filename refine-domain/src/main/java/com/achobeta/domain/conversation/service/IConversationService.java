package com.achobeta.domain.conversation.service;

import java.util.function.Consumer;

/**
 * @Auth : Malog
 * @Desc : 会话服务接口（纯Redis实现）
 * @Time : 2025/11/10
 */
public interface IConversationService {

    /**
     * 发送消息并获取AI回复（流式）
     *
     * @param conversationId 会话ID
     * @param userMessage 用户消息
     * @param contentCallback 流式回调函数
     * @return 是否发送成功
     */
    boolean sendMessageWithStream(String conversationId, String userMessage, Consumer<String> contentCallback);

    /**
     * 删除会话（清除Redis中的对话历史）
     *
     * @param conversationId 会话ID
     * @return 是否删除成功
     */
    boolean deleteConversation(String conversationId);
}