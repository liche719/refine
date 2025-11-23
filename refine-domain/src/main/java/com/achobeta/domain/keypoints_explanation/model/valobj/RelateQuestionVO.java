package com.achobeta.domain.keypoints_explanation.model.valobj;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RelateQuestionVO {
    private List<QuestionVO> qestions;
    private String note;
}
