package com.achobeta.domain.conversation.model.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Auth : Malog
 * @Desc : 会话消息实体
 * @Time : 2025/11/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessageEntity {

    /** 主键ID */
    private Long id;

    /** 会话ID */
    private String conversationId;

    /** 消息类型（1-用户消息，2-AI回复） */
    private Integer messageType;

    /** 消息内容 */
    private String messageContent;

    /** 消息顺序 */
    private Integer messageOrder;

    /** 创建时间 */
    private LocalDateTime createTime;

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        USER(1, "用户消息"),
        AI(2, "AI回复");

        private final Integer code;
        private final String desc;

        MessageType(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 创建用户消息
     */
    public static ConversationMessageEntity createUserMessage(String conversationId, String messageContent, Integer messageOrder) {
        return ConversationMessageEntity.builder()
                .conversationId(conversationId)
                .messageType(MessageType.USER.getCode())
                .messageContent(messageContent)
                .messageOrder(messageOrder)
                .createTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建AI回复消息
     */
    public static ConversationMessageEntity createAiMessage(String conversationId, String messageContent, Integer messageOrder) {
        return ConversationMessageEntity.builder()
                .conversationId(conversationId)
                .messageType(MessageType.AI.getCode())
                .messageContent(messageContent)
                .messageOrder(messageOrder)
                .createTime(LocalDateTime.now())
                .build();
    }

    /**
     * 是否为用户消息
     */
    public boolean isUserMessage() {
        return MessageType.USER.getCode().equals(this.messageType);
    }

    /**
     * 是否为AI消息
     */
    public boolean isAiMessage() {
        return MessageType.AI.getCode().equals(this.messageType);
    }
}