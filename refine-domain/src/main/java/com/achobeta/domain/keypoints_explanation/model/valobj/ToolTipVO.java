package com.achobeta.domain.keypoints_explanation.model.valobj;

import cn.hutool.core.date.DateTime;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToolTipVO {
    private int total; // 总题数
    private int count; // 剩余错题数
    private String lastReviewTime; // 最后一次复习时间
}
