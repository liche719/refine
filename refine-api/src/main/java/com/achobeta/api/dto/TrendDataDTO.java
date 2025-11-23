package com.achobeta.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrendDataDTO {
    private String date; // 如 "2025-11"
    private Integer studyCount; // 当月学习次数
    private Integer reviewCount; // 当月复习次数
}
