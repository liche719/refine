package com.achobeta.domain.mistake.adapter.repository;

/**
 * @Auth : Malog
 * @Desc : 错题笔记仓储接口
 * @Time : 2025/11/10
 */
public interface IStudyNoteRepository {

    /**
     * 更新错题笔记
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @param studyNote 学习笔记内容
     * @return 是否更新成功
     */
    boolean updateStudyNote(String userId, String questionId, String studyNote);

    /**
     * 获取错题笔记
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 学习笔记内容
     */
    String getStudyNote(String userId, String questionId);
}