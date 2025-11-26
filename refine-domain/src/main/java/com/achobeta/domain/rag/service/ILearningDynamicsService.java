package com.achobeta.domain.rag.service;

import com.achobeta.domain.rag.model.valobj.LearningDynamicVO;

import java.util.List;

/**
 * @Auth : Malog
 * @Desc : 学习动态分析服务接口
 * @Time : 2025/11/25
 */
public interface ILearningDynamicsService {
    
    /**
     * 分析用户学习动态（用户登录时触发）
     * 
     * @param userId 用户ID
     * @return 学习动态列表（最多3条）
     */
    List<LearningDynamicVO> analyzeUserLearningDynamics(String userId);
    
    /**
     * 获取用户最近N天的学习数据
     * 
     * @param userId 用户ID
     * @param days 天数
     * @return 学习数据摘要
     */
    String getUserLearningDataSummary(String userId, int days);
}