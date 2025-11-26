package com.achobeta.domain.overview.model.valobj;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyOverviewVO {
    /**
     *累计错题数量
     */
    private int questionsNum;
    /**
     * 复习巩固率（百分比）
     */
    private double reviewRate;
    /**
     * 易错知识点数量
     */
    private int hardQuestions;
    /**
     * 累计学习时长（小时）
     */
    private int studyTime;
}
