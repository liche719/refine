package com.achobeta.domain.rag.model.valobj;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Auth : Malog
 * @Desc : 学习统计值对象
 * @Time : 2025/11/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningStatisticsVO {
    
    /**
     * 总活动次数
     */
    private Integer totalActivities;
    
    /**
     * 行为类型数量
     */
    private Integer actionTypesCount;
    
    /**
     * 涉及科目数量
     */
    private Integer subjectsCount;
    
    /**
     * 活跃天数
     */
    private Integer activeDays;
    
    /**
     * 第一次活动时间
     */
    private LocalDateTime firstActivity;
    
    /**
     * 最后一次活动时间
     */
    private LocalDateTime lastActivity;
}