package com.achobeta.api.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsDTO {
    private List<Map<String, Integer>> subjectDistribution;   // 学科分布
    private List<Map<String, Integer>> knowledgeDistribution; // 错因分布
    private List<Integer> reviewTrend;            // 复习趋势（完成率）

}
