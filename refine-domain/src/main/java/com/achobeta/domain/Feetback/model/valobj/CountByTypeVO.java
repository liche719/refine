package com.achobeta.domain.Feetback.model.valobj;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountByTypeVO {
    /**
     * 错误类型名称: 学科/知识点
     */
    private String name;
    /**
     * 错误类型数量
     */
    private int count;
}
