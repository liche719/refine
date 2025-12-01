package com.achobeta.domain.ocr.service.impl;

import com.achobeta.domain.ocr.adapter.port.IMistakeQuestionRepository;
import com.achobeta.domain.ocr.model.entity.QuestionEntity;
import com.achobeta.domain.ocr.service.IMistakeQuestionService;
import com.achobeta.domain.user.event.UserUploadEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @Auth : Malog
 * @Desc : 错题领域服务实现
 * @Time : 2025/11/10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MistakeQuestionServiceImpl implements IMistakeQuestionService {

    private final IMistakeQuestionRepository mistakeQuestionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean saveMistakeQuestion(QuestionEntity questionEntity) {
        try {
            if (questionEntity == null) {
                log.warn("题目实体为空，无法保存错题");
                return false;
            }

            if (questionEntity.getQuestionId() == null || questionEntity.getQuestionId().isEmpty()) {
                log.warn("题目ID为空，无法保存错题");
                return false;
            }

            if (questionEntity.getUserId() == null || questionEntity.getUserId().isEmpty()) {
                log.warn("用户ID为空，无法保存错题");
                return false;
            }

            if (questionEntity.getQuestionText() == null || questionEntity.getQuestionText().isEmpty()) {
                log.warn("题目内容为空，无法保存错题");
                return false;
            }

            // 保存错题数据
            boolean success = mistakeQuestionRepository.save(questionEntity);

            if (success) {
                log.info("错题保存成功: userId={}, questionId={}",
                        questionEntity.getUserId(), questionEntity.getQuestionId());
            } else {
                log.error("错题保存失败: userId={}, questionId={}",
                        questionEntity.getUserId(), questionEntity.getQuestionId());
            }

            // 发布用户上传事件，由事件监听器处理向量存储
            try {
                UserUploadEvent uploadEvent = new UserUploadEvent(
                        this,
                        questionEntity.getUserId(),
                        questionEntity.getQuestionId(),
                        questionEntity.getQuestionText(),
                        questionEntity.getSubject(),
                        null  // knowledgePointId 暂时为空，可以从AI分析中获取
                );
                eventPublisher.publishEvent(uploadEvent);
                log.info("用户上传题目事件已发布，userId:{}, questionId:{}", questionEntity.getUserId(), questionEntity.getQuestionId());
            } catch (Exception e) {
                // 事件发布失败不应该影响OCR主流程，只记录日志
                log.error("发布用户上传题目事件时发生异常，userId:{} questionId:{}", questionEntity.getUserId(), questionEntity.getQuestionId(), e);
            }

            return success;
        } catch (Exception e) {
            log.error("保存错题数据时发生异常: userId={}, questionId={}",
                    questionEntity != null ? questionEntity.getUserId() : null,
                    questionEntity != null ? questionEntity.getQuestionId() : null, e);
            return false;
        }
    }

    @Override
    public void insertKnowledgePointAndSubject(String userId, String questionId, String knowledgePointId, String subject) {
        try {
            mistakeQuestionRepository.insertKnowledgePointAndSubject(userId, questionId, knowledgePointId, subject);
        } catch (Exception e) {
            log.error("插入知识点和学科时发生异常: questionId={}, knowledgePointId={}, subject={}",
                    questionId, knowledgePointId, subject, e);
        }
    }

}
