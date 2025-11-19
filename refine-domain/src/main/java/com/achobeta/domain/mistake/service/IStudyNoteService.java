package com.achobeta.domain.mistake.service;

import com.achobeta.domain.mistake.model.valobj.StudyNoteVO;

/**
 * @Auth : Malog
 * @Desc : 错题笔记服务接口
 * @Time : 2025/11/10
 */
public interface IStudyNoteService {

    /**
     * 更新错题笔记
     *
     * @param studyNoteVO 错题笔记值对象
     * @return 更新后的错题笔记值对象
     */
    StudyNoteVO updateStudyNote(StudyNoteVO studyNoteVO);

    /**
     * 获取错题笔记
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 错题笔记值对象
     */
    StudyNoteVO getStudyNote(String userId, String questionId);
}