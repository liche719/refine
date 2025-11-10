package com.achobeta.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Auth : Malog
 * @Desc : 错因管理响应DTO
 * @Time : 2025/11/10
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MistakeReasonResponseDTO implements Serializable {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 题目ID
     */
    private String questionId;

    /**
     * 是否粗心（0-未选择，1-已选择）
     */
    private Integer isCareless;

    /**
     * 是否知识点不熟悉（0-未选择，1-已选择）
     */
    private Integer isUnfamiliar;

    /**
     * 是否计算错误（0-未选择，1-已选择）
     */
    private Integer isCalculateErr;

    /**
     * 是否时间不足（0-未选择，1-已选择）
     */
    private Integer isTimeShortage;

    /**
     * 是否选择其他原因（0-未选择，1-已选择）
     */
    private Integer otherReason;

    /**
     * 其他原因描述
     */
    private String otherReasonText;

    /**
     * 操作是否成功
     */
    private Boolean success;

    /**
     * 操作消息
     */
    private String message;
}
