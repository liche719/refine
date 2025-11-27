package com.achobeta.domain.mistake.service.impl;

import com.achobeta.domain.mistake.adapter.repository.IStudyNoteRepository;
import com.achobeta.domain.mistake.model.valobj.StudyNoteVO;
import com.achobeta.domain.mistake.service.IStudyNoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Auth : Malog
 * @Desc : 错题笔记服务实现
 * @Time : 2025/11/10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudyNoteServiceImpl implements IStudyNoteService {

    private final IStudyNoteRepository studyNoteRepository;

    @Override
    public StudyNoteVO updateStudyNote(StudyNoteVO studyNoteVO) {
        try {
            // 验证基本字段
            if (!studyNoteVO.isValid()) {
                return studyNoteVO;
            }

            // 更新数据库
            boolean success = studyNoteRepository.updateStudyNote(
                    studyNoteVO.getUserId(),
                    studyNoteVO.getQuestionId(),
                    studyNoteVO.getStudyNote());

            if (success) {
                log.info("更新错题笔记成功: userId={}, questionId={}",
                        studyNoteVO.getUserId(), studyNoteVO.getQuestionId());
                return StudyNoteVO.success(
                        studyNoteVO.getUserId(),
                        studyNoteVO.getQuestionId(),
                        studyNoteVO.getStudyNote());
            } else {
                log.warn("更新错题笔记失败: userId={}, questionId={}",
                        studyNoteVO.getUserId(), studyNoteVO.getQuestionId());
                return StudyNoteVO.error(
                        studyNoteVO.getUserId(),
                        studyNoteVO.getQuestionId(),
                        "更新笔记失败，可能是错题记录不存在");
            }
        } catch (Exception e) {
            log.error("更新错题笔记时发生异常: userId={}, questionId={}",
                    studyNoteVO.getUserId(), studyNoteVO.getQuestionId(), e);
            return StudyNoteVO.error(
                    studyNoteVO.getUserId(),
                    studyNoteVO.getQuestionId(),
                    "系统异常: " + e.getMessage());
        }
    }

    @Override
    public StudyNoteVO getStudyNote(String userId, String questionId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return StudyNoteVO.error(userId, questionId, "用户ID不能为空");
            }

            if (questionId == null || questionId.trim().isEmpty()) {
                return StudyNoteVO.error(userId, questionId, "题目ID不能为空");
            }

            String studyNote = studyNoteRepository.getStudyNote(userId, questionId);
            if (studyNote == null) {
                log.info("未找到错题笔记: userId={}, questionId={}", userId, questionId);
                return StudyNoteVO.error(userId, questionId, "未找到对应的错题记录");
            }

            log.info("获取错题笔记成功: userId={}, questionId={}", userId, questionId);
            return StudyNoteVO.success(userId, questionId, studyNote);
        } catch (Exception e) {
            log.error("获取错题笔记时发生异常: userId={}, questionId={}", userId, questionId, e);
            return StudyNoteVO.error(userId, questionId, "系统异常: " + e.getMessage());
        }
    }
}