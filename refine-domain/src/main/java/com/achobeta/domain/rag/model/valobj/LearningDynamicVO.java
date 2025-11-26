package com.achobeta.domain.rag.model.valobj;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * @Auth : Malog
 * @Desc : 学习动态值对象
 * @Time : 2025/11/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningDynamicVO {
    
    /**
     * 动态类型：progress(学习进步), weakness(发现薄弱点), achievement(学习成就)
     */
    private String type;
    
    /**
     * 动态标题
     */
    private String title;
    
    /**
     * 动态描述
     */
    private String description;
    
    /**
     * 相关科目
     */
    private String subject;
    
    /**
     * 重要程度 (1-5, 5最重要)
     */
    private Integer priority;
    
    /**
     * 建议行动
     */
    private String suggestion;
    
    /**
     * 相关题目数量
     */
    private Integer relatedQuestionCount;
}