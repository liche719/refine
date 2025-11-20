package com.achobeta.domain.Feetback.model.valobj;

import lombok.*;


/**
 * 待复习题目数量
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OverdueCountVO {
    private int count;
    private String description;
}
