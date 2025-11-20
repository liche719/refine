package com.achobeta.infrastructure.adapter.repository;

import com.achobeta.domain.mistake.adapter.repository.IStudyNoteRepository;
import com.achobeta.infrastructure.dao.IMistakeQuestionDao;
import com.achobeta.infrastructure.dao.po.MistakeQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * @Auth : Malog
 * @Desc : 错题笔记仓储实现
 * @Time : 2025/11/10
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class StudyNoteRepository implements IStudyNoteRepository {

    private final IMistakeQuestionDao mistakeQuestionDao;

    @Override
    public boolean updateStudyNote(String userId, String questionId, String studyNote) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                log.warn("用户ID为空，无法更新笔记");
                return false;
            }

            if (questionId == null || questionId.trim().isEmpty()) {
                log.warn("题目ID为空，无法更新笔记");
                return false;
            }

            if (studyNote == null) {
                log.warn("笔记内容为null，无法更新笔记");
                return false;
            }

            // 先检查错题记录是否存在
            MistakeQuestion existingQuestion = mistakeQuestionDao.selectByUserIdAndQuestionId(userId, questionId);
            if (existingQuestion == null) {
                log.warn("未找到对应的错题记录: userId={}, questionId={}", userId, questionId);
                return false;
            }

            // 更新笔记
            int affectedRows = mistakeQuestionDao.updateStudyNote(userId, questionId, studyNote);
            boolean success = affectedRows > 0;

            if (success) {
                log.info("更新错题笔记成功: userId={}, questionId={}, affectedRows={}",
                        userId, questionId, affectedRows);
            } else {
                log.warn("更新错题笔记失败: userId={}, questionId={}, affectedRows={}",
                        userId, questionId, affectedRows);
            }

            return success;
        } catch (Exception e) {
            log.error("更新错题笔记时发生异常: userId={}, questionId={}", userId, questionId, e);
            return false;
        }
    }

    @Override
    public String getStudyNote(String userId, String questionId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                log.warn("用户ID为空，无法获取笔记");
                return null;
            }

            if (questionId == null || questionId.trim().isEmpty()) {
                log.warn("题目ID为空，无法获取笔记");
                return null;
            }

            MistakeQuestion mistakeQuestion = mistakeQuestionDao.selectByUserIdAndQuestionId(userId, questionId);
            if (mistakeQuestion == null) {
                log.info("未找到对应的错题记录: userId={}, questionId={}", userId, questionId);
                return null;
            }

            String studyNote = mistakeQuestion.getStudyNote();
            log.info("获取错题笔记成功: userId={}, questionId={}, noteLength={}",
                    userId, questionId, studyNote != null ? studyNote.length() : 0);

            return studyNote;
        } catch (Exception e) {
            log.error("获取错题笔记时发生异常: userId={}, questionId={}", userId, questionId, e);
            return null;
        }
    }
}