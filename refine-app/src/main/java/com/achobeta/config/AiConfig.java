package com.achobeta.config;

import com.achobeta.domain.aisuggession.service.ConsultantService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class AiConfig {
    @Autowired
    private OpenAiChatModel model;
    @Autowired
    private ChatModel chatModel;

    @Bean
    public ConsultantService consultantService() {
        return AiServices.builder(ConsultantService.class)
                .chatModel(chatModel)
                //.chatModel(model)
                .build();
    }
}
