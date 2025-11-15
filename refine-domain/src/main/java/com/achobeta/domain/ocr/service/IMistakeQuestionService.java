package com.achobeta.domain.ocr.service;

import com.achobeta.domain.ocr.model.entity.QuestionEntity;

/**
 * @Auth : Malog
 * @Desc : 错题领域服务接口
 * @Time : 2025/11/10
 */
public interface IMistakeQuestionService {

    /**
     * 保存错题数据
     *
     * @param questionEntity 题目实体
     * @return 是否保存成功
     */
    boolean saveMistakeQuestion(QuestionEntity questionEntity);
}
