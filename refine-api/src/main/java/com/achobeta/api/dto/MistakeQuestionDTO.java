package com.achobeta.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MistakeQuestionDTO {

    // 用户id
    private String userId;
    // redis键
    private String questionId;
    // 识别出的题目内容（如果有选项则包含选项）
    private String questionContent;
    // 答案
    private String answer;
    // 科目
    private String subject;
    // 知识点id
    private String knowledgePointId;

}
