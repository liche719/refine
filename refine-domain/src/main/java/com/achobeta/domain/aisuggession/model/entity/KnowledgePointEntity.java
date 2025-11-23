package com.achobeta.domain.aisuggession.model.entity;

import lombok.*;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgePointEntity {
    /**
     * 题目内容
     */
    private String context;
    /**
     * 所属知识点
     */
    private String knowledgePoint;

    /**
     * 题目状态
     */
    private int status;
}
