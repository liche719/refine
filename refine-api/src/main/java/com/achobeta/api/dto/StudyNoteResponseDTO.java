package com.achobeta.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Auth : Malog
 * @Desc : 错题笔记响应DTO
 * @Time : 2025/11/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyNoteResponseDTO implements Serializable {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 题目ID
     */
    private String questionId;

    /**
     * 学习笔记内容
     */
    private String studyNote;

    /**
     * 操作是否成功
     */
    private Boolean success;

    /**
     * 响应消息
     */
    private String message;
}