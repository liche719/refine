package com.achobeta.domain.question.model.po;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MistakeKnowledgePO {
    private String subject;
    private String knowledgeId;
}
