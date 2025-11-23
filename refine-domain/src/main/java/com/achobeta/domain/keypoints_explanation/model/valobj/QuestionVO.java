package com.achobeta.domain.keypoints_explanation.model.valobj;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionVO {
    private int id;// 问题id
    private String question;//  问题
}
