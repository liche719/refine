package com.achobeta.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseDTO {

    /**
     * redis中的键
     */
    private String questionId;

    // 题目内容(含选项)
    private String content;

    // 答案
    private String answer;


}