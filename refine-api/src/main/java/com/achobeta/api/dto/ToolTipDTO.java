package com.achobeta.api.dto;

import cn.hutool.core.date.DateTime;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToolTipDTO {
    private double degreeOfProficiency; // 熟练度
    private int count; // 错题数
    private String lastReviewTime; // 最后一次复习时间
}
