package com.achobeta.domain.Feetback.model.valobj;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrickyKnowledgePointVO {
    /**
     * 知识点id
     */
    private String knowledgeId;
    /**
     * 知识点名称
     */
    private String knowledgeName;
}
