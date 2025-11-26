package com.achobeta.domain.rag.service;

import com.achobeta.domain.rag.model.valobj.LearningInsightVO;
import com.achobeta.domain.rag.model.valobj.SimilarQuestionVO;

import java.util.List;

/**
 * @Auth : Malog
 * @Desc : 向量服务接口
 * @Time : 2025/11/25 21:03
 */
public interface IVectorService {
    /**
     * 存储学习行为向量
     */
    boolean storeLearningVector(String userId, String questionId, String questionContent,
                                String actionType, String subject, Integer knowledgePointId);

    /**
     * 检索相似题目
     */
    List<SimilarQuestionVO> searchSimilarQuestions(String userId, String queryText, int limit);

    /**
     * 获取用户学习薄弱点分析
     */
    List<LearningInsightVO> getUserWeaknesses(String userId);

    /**
     * 获取用户学习推荐
     */
    List<LearningInsightVO> getUserRecommendations(String userId);

    /**
     * 批量分析用户学习模式
     */
    void analyzeUserLearningPatterns(String userId);
}
