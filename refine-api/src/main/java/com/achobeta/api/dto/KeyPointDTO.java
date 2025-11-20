package com.achobeta.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeyPointDTO {
    /**
     * 知识点
     */
    private String knowledgePoint;
    /**
     * 复习原因
     */
    private String reviewReason;
}
