package com.achobeta.domain.question.service;

import com.achobeta.api.dto.MistakeQuestionDTO;
import com.achobeta.api.dto.QuestionResponseDTO;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface IQuestionService {

    QuestionResponseDTO questionGeneration(String userId, Integer knowledgePointId);

    void recordMistakeQuestion(String userId, String questionId);

    void removeQuestionCache(String questionId);

    Flux<ServerSentEvent<String>> aiJudge(String userId, String questionId, String answer);

}
