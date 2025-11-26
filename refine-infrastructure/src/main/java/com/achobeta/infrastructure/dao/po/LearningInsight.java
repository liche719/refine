package com.achobeta.infrastructure.dao.po;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Auth : Malog
 * @Desc : 学习洞察数据库实体
 * @Time : 2025/11/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningInsight {
    
    private Long id;
    private String userId;
    private String insightType;
    private String title;
    private String description;
    private BigDecimal confidenceScore;
    private String relatedQuestions; // JSON字符串存储
    private LocalDateTime createdAt;
    private Boolean isActive;
}