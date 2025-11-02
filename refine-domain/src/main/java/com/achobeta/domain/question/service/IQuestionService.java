package com.achobeta.domain.question.service;

import com.achobeta.api.dto.question.QuestionResponseDTO;

public interface IQuestionService {

    QuestionResponseDTO questionGeneration(String userId, Integer knowledgePointId);

    void recordMistakeQuestion(String userId, String questionId);

}
