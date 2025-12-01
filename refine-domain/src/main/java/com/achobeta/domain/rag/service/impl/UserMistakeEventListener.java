package com.achobeta.domain.rag.service.impl;

import com.achobeta.domain.rag.service.IVectorService;
import com.achobeta.types.event.UserMistakeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户错误行为事件监听器
 * 负责将用户的错误行为异步写入向量库，用于后续的学习动态追踪
 */
@Slf4j
@Service
public class UserMistakeEventListener {

    @Autowired
    @Qualifier("weaviateVectorRepository")
    private IVectorService vectorService;

    /**
     * 监听用户错误行为事件，异步处理
     */
    @Async
    @EventListener
    public void handleUserMistakeEvent(UserMistakeEvent event) {
        try {
            log.info("处理用户错误行为事件，userId: {}, mistakeType: {}, sessionId: {}", 
                    event.getUserId(), event.getMistakeType().getCode(), event.getSessionId());

            // 构建向量数据
            Map<String, Object> vectorData = buildVectorData(event);
            
            // 生成唯一的向量ID
            String vectorId = generateVectorId(event);
            
            // 异步写入向量库，使用统一的行为类型
            boolean success = vectorService.storeLearningVector(
                    event.getUserId(),
                    vectorId, // 使用vectorId作为questionId
                    event.getQuestionContent() != null ? event.getQuestionContent() : "AI求助",
                    "qa", // 统一使用qa作为行为类型
                    event.getSubject(),
                    null // knowledgePointId暂时为null
            );
            
            if (success) {
                log.info("用户错误行为已成功写入向量库，userId: {}, vectorId: {}", event.getUserId(), vectorId);
            } else {
                log.warn("向量库写入失败，userId: {}, vectorId: {}", event.getUserId(), vectorId);
            }
            
            log.info("用户错误行为已写入向量库，userId: {}, vectorId: {}", event.getUserId(), vectorId);
            
        } catch (Exception e) {
            log.error("处理用户错误行为事件失败，userId: {}, sessionId: {}", 
                    event.getUserId(), event.getSessionId(), e);
        }
    }

    /**
     * 构建向量数据
     */
    private Map<String, Object> buildVectorData(UserMistakeEvent event) {
        Map<String, Object> data = new HashMap<>();
        
        // 基本信息
        data.put("userId", event.getUserId());
        data.put("mistakeType", event.getMistakeType().getCode());
        data.put("mistakeDescription", event.getMistakeType().getDescription());
        data.put("sessionId", event.getSessionId());
        
        // 时间信息
        data.put("eventTime", event.getEventTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        data.put("timestamp", System.currentTimeMillis());
        
        // 学习内容信息
        if (event.getQuestionContent() != null) {
            data.put("questionContent", event.getQuestionContent());
            data.put("questionLength", event.getQuestionContent().length());
        }
        
        if (event.getSubject() != null) {
            data.put("subject", event.getSubject());
        }
        
        if (event.getKnowledgePoint() != null) {
            data.put("knowledgePoint", event.getKnowledgePoint());
        }
        
        // 错误详情
        if (event.getMistakeDescription() != null) {
            data.put("detailedDescription", event.getMistakeDescription());
        }
        
        // 上下文信息
        if (event.getContextInfo() != null) {
            data.put("contextInfo", event.getContextInfo());
        }
        
        // 行为分类
        data.put("behaviorCategory", "mistake");
        data.put("learningPhase", determineLearningPhase(event));
        
        return data;
    }

    /**
     * 生成向量ID
     */
    private String generateVectorId(UserMistakeEvent event) {
        return String.format("mistake_%s_%s_%d", 
                event.getUserId(), 
                event.getSessionId(), 
                System.currentTimeMillis());
    }

    /**
     * 判断学习阶段
     */
    private String determineLearningPhase(UserMistakeEvent event) {
        switch (event.getMistakeType()) {
            case AI_HELP_REQUEST:
                return "seeking_help";
            case CONCEPT_MISUNDERSTANDING:
                return "concept_learning";
            case CALCULATION_ERROR:
                return "practice";
            case METHOD_ERROR:
                return "problem_solving";
            default:
                return "unknown";
        }
    }
}