package com.achobeta.infrastructure.adapter.port;

import com.achobeta.domain.question.adapter.port.AiGenerationService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建AiGenerationService的工厂类
 */
@Configuration
public class AiGenerationServiceFactory {

    @Resource
    private ChatModel qwenChatModel;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    @Bean
    public AiGenerationService aiGenerationService() {
        AiGenerationService build = AiServices.builder(AiGenerationService.class)
                .chatModel(qwenChatModel)
                .streamingChatModel(qwenStreamingChatModel) //流式输出
                .build();
        return build;
    }

}
