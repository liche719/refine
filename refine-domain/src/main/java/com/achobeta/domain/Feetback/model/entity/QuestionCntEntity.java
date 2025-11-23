package com.achobeta.domain.Feetback.model.entity;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCntEntity {

    /**
     * 问题数量
     */
    private int count;
    /**
     * 问题描述
     */
    private String description;
}
