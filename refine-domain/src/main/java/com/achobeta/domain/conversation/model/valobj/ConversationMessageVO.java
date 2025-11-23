package com.achobeta.domain.conversation.model.valobj;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Auth : Malog
 * @Desc : 会话消息值对象
 * @Time : 2025/11/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessageVO {

    /** 消息ID */
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

    /**
     * 获取消息类型描述
     */
    public String getMessageTypeDesc() {
        if (MessageType.USER.getCode().equals(this.messageType)) {
            return MessageType.USER.getDesc();
        } else if (MessageType.AI.getCode().equals(this.messageType)) {
            return MessageType.AI.getDesc();
        }
        return "未知类型";
    }
}