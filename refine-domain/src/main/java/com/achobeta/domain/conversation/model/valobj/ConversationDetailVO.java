package com.achobeta.domain.conversation.model.valobj;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Auth : Malog
 * @Desc : 会话详情值对象
 * @Time : 2025/11/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDetailVO {

    /** 会话ID */
    private String conversationId;

    /** 用户ID */
    private String userId;

    /** 错题ID */
    private String questionId;

    /** 题目内容 */
    private String questionContent;

    /** AI解答内容 */
    private String aiSolution;

    /** 会话消息列表 */
    private List<ConversationMessageVO> messages;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 操作是否成功 */
    private Boolean success;

    /** 错误信息 */
    private String message;

    /**
     * 创建成功响应
     */
    public static ConversationDetailVO success(ConversationDetailVO detail) {
        detail.setSuccess(true);
        detail.setMessage("操作成功");
        return detail;
    }

    /**
     * 创建错误响应
     */
    public static ConversationDetailVO error(String conversationId, String message) {
        return ConversationDetailVO.builder()
                .conversationId(conversationId)
                .success(false)
                .message(message)
                .build();
    }

    /**
     * 验证基本字段
     */
    public boolean isValid() {
        if (conversationId == null || conversationId.trim().isEmpty()) {
            this.success = false;
            this.message = "会话ID不能为空";
            return false;
        }
        if (userId == null || userId.trim().isEmpty()) {
            this.success = false;
            this.message = "用户ID不能为空";
            return false;
        }
        return true;
    }
}