package com.achobeta.domain.Feetback.model.valobj;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsVO {
    private List<CountByTypeVO> subjectDistribution;   // 学科分布
    private List<CountByTypeVO> knowledgeDistribution; // 知识点分布
    private List<ReviewTrendVO> reviewTrend;            // 复习趋势（完成率）
}
