package com.achobeta.domain.Feetback.model.entity;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MistakeQuestionEntity {
    /**
     * 错误题目ID
     */
    private Integer id;
    /**
     * 错误题目内容
     */
    private String context;
    /**
     * 错误原因
     */
    private Integer isCareless;
    private Integer isUnfamiliar;
    private Integer isCalculateError;
    private Integer isTimeShortage;
    /**
     * 创建时间
     */
    private LocalDate createTime;
    /**
     * 所属科目
     */
    private String subject;
    /**
     * 所属知识点
     */
    private String knowledgePoint;
    /**
     * 创建者ID
     */
    private String userId;
    /**
     * 更新时间
     */
    private String updateTime;

}
