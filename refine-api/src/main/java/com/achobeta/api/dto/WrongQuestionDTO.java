package com.achobeta.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WrongQuestionDTO {
    private int updateCount;
    private String mainQuestion;
    private int reviewCount;
}
