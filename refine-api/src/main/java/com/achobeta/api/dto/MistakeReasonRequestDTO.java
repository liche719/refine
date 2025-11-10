package com.achobeta.api.dto;

import com.achobeta.types.annotation.FieldDesc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Auth : Malog
 * @Desc : 错因管理请求DTO
 * @Time : 2025/11/10
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MistakeReasonRequestDTO implements Serializable {

    @NotBlank(message = "用户ID不能为空")
    @FieldDesc(name = "用户ID")
    private String userId;

    @NotBlank(message = "题目ID不能为空")
    @FieldDesc(name = "题目ID")
    private String questionId;

    @FieldDesc(name = "是否粗心")
    private Integer isCareless;

    @FieldDesc(name = "是否知识点不熟悉")
    private Integer isUnfamiliar;

    @FieldDesc(name = "是否计算错误")
    private Integer isCalculateErr;

    @FieldDesc(name = "是否时间不足")
    private Integer isTimeShortage;

    @FieldDesc(name = "是否选择其他原因")
    private Integer otherReason;

    @FieldDesc(name = "其他原因描述")
    private String otherReasonText;

    /**
     * 切换错因状态的方法
     * 将指定的错因在0和1之间切换
     * @param reasonName 错因名称
     */
    public void toggleReason(String reasonName) {
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
    }
}
