package com.achobeta.domain.question.adapter.repository;


import com.achobeta.domain.question.model.entity.MistakeQuestionEntity;
import com.achobeta.domain.question.model.po.MistakeKnowledgePO;

/**
 * 错题仓储接口（领域层定义，基础设施层实现）
 */
public interface IMistakeRepository {
    // 保存错题
    void save(MistakeQuestionEntity mistakeEntity);

    MistakeKnowledgePO findSubjectAndKnowledgeIdById(Integer mistakeQuestionId);
}