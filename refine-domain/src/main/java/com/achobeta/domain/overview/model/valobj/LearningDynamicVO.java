package com.achobeta.domain.overview.model.valobj;

import com.achobeta.api.dto.TrendDataDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearningDynamicVO {
    private Integer questionsCount; // 最近一周错题数
    private Integer weeklyReviewCount; // 本周复盘数
    private List<TrendDataDTO> trendData; // 折线图数据（时间 vs 数值）
}
