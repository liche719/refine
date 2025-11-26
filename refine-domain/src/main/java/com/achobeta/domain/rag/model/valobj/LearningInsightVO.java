package com.achobeta.domain.rag.model.valobj;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Auth : Malog
 * @Desc : 学习洞察值对象
 * @Time : 2025/11/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningInsightVO {
    
    /**
     * 洞察类型：weakness(薄弱点), strength(优势), recommendation(推荐)
     */
    private String type;
    
    /**
     * 洞察标题
     */
    private String title;
    
    /**
     * 洞察描述
     */
    private String description;
    
    /**
     * 置信度分数 (0.0-1.0)
     */
    private Double confidenceScore;
    
    /**
     * 相关题目ID列表
     */
    private List<String> relatedQuestions;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 是否激活状态
     */
    private Boolean isActive;
    
    /**
     * 额外的元数据信息
     */
    private String metadata;
}