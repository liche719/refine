package com.achobeta.types.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户错误行为事件
 * 用于追踪用户在学习过程中遇到的问题和错误
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMistakeEvent {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 错误类型
     */
    private MistakeType mistakeType;
    
    /**
     * 问题内容
     */
    private String questionContent;
    
    /**
     * 错误描述
     */
    private String mistakeDescription;
    
    /**
     * 相关科目
     */
    private String subject;
    
    /**
     * 知识点
     */
    private String knowledgePoint;
    
    /**
     * 事件发生时间
     */
    private LocalDateTime eventTime;
    
    /**
     * 会话ID（用于关联同一次学习会话）
     */
    private String sessionId;
    
    /**
     * 额外的上下文信息
     */
    private String contextInfo;
    
    /**
     * 错误类型枚举
     */
    public enum MistakeType {
        /**
         * 求助AI - 表示用户遇到不会的题目需要AI帮助
         */
        AI_HELP_REQUEST("ai_help", "AI求助"),
        
        /**
         * 概念理解错误
         */
        CONCEPT_MISUNDERSTANDING("concept_error", "概念理解错误"),
        
        /**
         * 计算错误
         */
        CALCULATION_ERROR("calc_error", "计算错误"),
        
        /**
         * 方法选择错误
         */
        METHOD_ERROR("method_error", "方法选择错误"),
        
        /**
         * 其他错误
         */
        OTHER("other", "其他错误");
        
        private final String code;
        private final String description;
        
        MistakeType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
}