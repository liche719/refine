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
     * 错误题目id
     */
    private Integer id;
    /**
     * 错误题目内容
     */
    private String question_content;
    /**
     * 错误原因
     */
    private Integer is_careless;
    private Integer is_unfamiliar;
    private Integer is_calculate_err;
    private Integer is_time_shortage;
    private String other_reason;
    /**
     * 创建时间
     */
    private LocalDate update_time;
    /**
     * 所属科目
     */
    private String subject;
    /**
     * 所属知识点
     */
    private String knowledge_desc;


}
