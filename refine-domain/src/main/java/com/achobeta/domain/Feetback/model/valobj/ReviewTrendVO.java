package com.achobeta.domain.Feetback.model.valobj;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewTrendVO {
    private String month;           // 月份
    private int total;            // 当月新增错题数
    private int reviewed;         // 当月错题中已复习错题数
    private Double completionRate; // 完成率 = reviewed / total
}
