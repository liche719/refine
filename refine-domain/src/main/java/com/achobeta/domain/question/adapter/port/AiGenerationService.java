package com.achobeta.domain.question.adapter.port;

import com.achobeta.api.dto.QuestionResponseDTO;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

/**
 * 语言模型接口
 */
public interface AiGenerationService {

    @SystemMessage(fromResource = "AiGeneration.txt")
    QuestionResponseDTO Generation(@MemoryId String subject, @UserMessage String message);

    //流式输出
    @SystemMessage(fromResource = "AiAnalyze.txt")
    Flux<String> aiJudgeStream(@MemoryId String subject, @UserMessage String message);

    // 会话
    String chat(String message);

}
