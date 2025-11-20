package com.achobeta.domain.keypoints_explanation.model.valobj;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WrongQuestionVO {
    private int updateCount = 0;
    private int reviewCount = 0;
}
