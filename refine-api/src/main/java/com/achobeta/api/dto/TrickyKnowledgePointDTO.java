package com.achobeta.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrickyKnowledgePointDTO {
    /**
     * 知识点id
     */
    private String knowledgeId;
    /**
     * 知识点名称
     */
    private String knowledgeName;
}
