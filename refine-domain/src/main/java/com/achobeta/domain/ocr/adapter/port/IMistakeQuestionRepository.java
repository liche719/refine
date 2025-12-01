package com.achobeta.domain.ocr.adapter.port;

import com.achobeta.domain.ocr.model.entity.QuestionEntity;

import java.util.List;

/**
 * @Auth : Malog
 * @Desc : 错题仓储接口
 * @Time : 2025/11/10
 */
public interface IMistakeQuestionRepository {

    /**
     * 保存错题数据
     *
     * @param questionEntity 题目实体
     * @return 是否保存成功
     */
    boolean save(QuestionEntity questionEntity);

    /**
     * 根据题目ID查询错题
     *
     * @param questionId 题目ID
     * @return 题目实体
     */
    QuestionEntity findByQuestionId(String questionId);

    /**
     * 根据用户ID查询错题列表
     *
     * @param userId 用户ID
     * @return 题目实体列表
     */
    List<QuestionEntity> findByUserId(String userId);

    void insertKnowledgePointAndSubject(String questionId, String knowledgePointId, String subject);
}
