package com.achobeta.api.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RelateQeustionDTO {
    private List<QuestionDTO> questions;
    private String note;
}
