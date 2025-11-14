package com.achobeta.domain.question.adapter.port;

import com.achobeta.api.dto.question.QuestionResponseDTO;
import dev.langchain4j.service.SystemMessage;

/**
 * 语言模型接口
 */
public interface AiGenerationService {

    @SystemMessage(fromResource = "AiGeneration.txt")
    QuestionResponseDTO Generation(String message);



}
