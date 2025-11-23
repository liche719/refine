package com.achobeta.domain.aisuggession.model.valobj;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeyPointVO {
    /**
     * 知识点
     */
    private String knowledgePoint;
    /**
     * 复习原因
     */
    private String reviewReason;
}
