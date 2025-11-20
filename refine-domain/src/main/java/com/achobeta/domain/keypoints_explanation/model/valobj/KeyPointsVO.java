package com.achobeta.domain.keypoints_explanation.model.valobj;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeyPointsVO {
    /**
     * 知识点id
     */
    private int id;
    /**
     * 知识点内容
     */
    private String keyPoints;
}
