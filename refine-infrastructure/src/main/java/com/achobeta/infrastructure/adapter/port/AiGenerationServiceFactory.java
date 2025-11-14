package com.achobeta.infrastructure.adapter.port;

import com.achobeta.domain.question.adapter.port.AiGenerationService;
import dev.langchain4j.model.chat.ChatModel;
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

    @Bean
    public AiGenerationService aiGenerationService() {
        return AiServices.create(AiGenerationService.class, qwenChatModel);
    }

}
