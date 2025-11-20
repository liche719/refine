package com.achobeta.domain.mistake.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * @Auth : Malog
 * @Desc : 错因值对象
 * @Time : 2025/11/10
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MistakeReasonVO {

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

    /**
     * 有效的错因名称列表
     */
    private static final List<String> VALID_REASONS = Arrays.asList(
            "isCareless", "isUnfamiliar", "isCalculateErr", "isTimeShortage", "otherReason"
    );

    /**
     * 切换错因状态
     *
     * @param reasonName 错因名称
     * @return 是否切换成功
     */
    public boolean toggleReason(String reasonName) {
        if (!VALID_REASONS.contains(reasonName)) {
            this.success = false;
            this.message = "无效的错因名称: " + reasonName;
            return false;
        }

        switch (reasonName) {
            case "isCareless":
                this.isCareless = this.isCareless == null || this.isCareless == 0 ? 1 : 0;
                break;
            case "isUnfamiliar":
                this.isUnfamiliar = this.isUnfamiliar == null || this.isUnfamiliar == 0 ? 1 : 0;
                break;
            case "isCalculateErr":
                this.isCalculateErr = this.isCalculateErr == null || this.isCalculateErr == 0 ? 1 : 0;
                break;
            case "isTimeShortage":
                this.isTimeShortage = this.isTimeShortage == null || this.isTimeShortage == 0 ? 1 : 0;
                break;
            case "otherReason":
                this.otherReason = this.otherReason == null || this.otherReason == 0 ? 1 : 0;
                // 如果关闭其他原因，清空文本内容
                if (this.otherReason == 0) {
                    this.otherReasonText = null;
                }
                break;
        }

        this.success = true;
        this.message = "错因状态切换成功";
        return true;
    }

    /**
     * 更新其他原因文本
     *
     * @param otherReasonText 其他原因文本
     * @return 是否更新成功
     */
    public boolean updateOtherReasonText(String otherReasonText) {
        // 数据安全校验：只有当otherReason为1时才能更新文本内容
        if (this.otherReason != 1) {
            this.success = false;
            this.message = "只有选择了其他原因才能填写具体描述";
            return false;
        }

        // 校验文本内容
        if (otherReasonText == null || otherReasonText.trim().isEmpty()) {
            this.success = false;
            this.message = "其他原因描述不能为空";
            return false;
        }

        this.otherReasonText = otherReasonText;
        this.success = true;
        this.message = "其他原因更新成功";
        return true;
    }

    /**
     * 验证基本字段
     *
     * @return 是否有效
     */
    public boolean isValid() {
        if (userId == null || userId.trim().isEmpty()) {
            this.success = false;
            this.message = "用户ID不能为空";
            return false;
        }

        if (questionId == null || questionId.trim().isEmpty()) {
            this.success = false;
            this.message = "题目ID不能为空";
            return false;
        }

        // 设置默认值
        if (isCareless == null) isCareless = 0;
        if (isUnfamiliar == null) isUnfamiliar = 0;
        if (isCalculateErr == null) isCalculateErr = 0;
        if (isTimeShortage == null) isTimeShortage = 0;
        if (otherReason == null) otherReason = 0;

        this.success = true;
        this.message = "数据验证成功";
        return true;
    }

    /**
     * 创建错误响应
     */
    public static MistakeReasonVO error(String userId, String questionId, String message) {
        return MistakeReasonVO.builder()
                .userId(userId)
                .questionId(questionId)
                .success(false)
                .message(message)
                .build();
    }

    /**
     * 创建成功响应
     */
    public static MistakeReasonVO success(MistakeReasonVO data) {
        return MistakeReasonVO.builder()
                .userId(data.getUserId())
                .questionId(data.getQuestionId())
                .isCareless(data.getIsCareless())
                .isUnfamiliar(data.getIsUnfamiliar())
                .isCalculateErr(data.getIsCalculateErr())
                .isTimeShortage(data.getIsTimeShortage())
                .otherReason(data.getOtherReason())
                .otherReasonText(data.getOtherReasonText())
                .success(true)
                .message(data.getMessage() != null ? data.getMessage() : "操作成功")
                .build();
    }
}
