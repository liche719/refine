package com.achobeta.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OverdueReviewDTO {
    /**
     * 待复习的题目数量
     */
    private int count;
    /**
     * 描述
     */
    private String description;
}
