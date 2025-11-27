package com.achobeta.domain.rag.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auth : Malog
 * @Desc : 相似问题值对象
 * @Time : 2025/11/25 21:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarQuestionVO {

    private String questionId;
    private String questionContent;
    private String actionType;
    private String subject;
    private Double similarity;
    private String createdAt;

}