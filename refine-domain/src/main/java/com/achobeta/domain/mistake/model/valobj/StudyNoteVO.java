package com.achobeta.domain.mistake.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auth : Malog
 * @Desc : 错题笔记值对象
 * @Time : 2025/11/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyNoteVO {

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

    /**
     * 验证基本字段是否有效
     */
    public boolean isValid() {
        if (userId == null || userId.trim().isEmpty()) {
            this.success = false;
            this.message = "用户ID不能为空";
            return false;
        }

        if (questionId == null || questionId.trim().isEmpty()) {
            this.success = false;
            this.message = "题目ID不能为空";
            return false;
        }

        if (studyNote == null || studyNote.trim().isEmpty()) {
            this.success = false;
            this.message = "笔记内容不能为空";
            return false;
        }

        if (studyNote.length() > 2000) {
            this.success = false;
            this.message = "笔记内容不能超过2000字符";
            return false;
        }

        return true;
    }

    /**
     * 创建成功响应
     */
    public static StudyNoteVO success(String userId, String questionId, String studyNote) {
        return StudyNoteVO.builder()
                .userId(userId)
                .questionId(questionId)
                .studyNote(studyNote)
                .success(true)
                .message("操作成功")
                .build();
    }

    /**
     * 创建错误响应
     */
    public static StudyNoteVO error(String userId, String questionId, String message) {
        return StudyNoteVO.builder()
                .userId(userId)
                .questionId(questionId)
                .success(false)
                .message(message)
                .build();
    }
}