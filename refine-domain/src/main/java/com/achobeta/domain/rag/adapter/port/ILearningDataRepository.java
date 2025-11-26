package com.achobeta.domain.rag.adapter.port;

import com.achobeta.domain.rag.model.entity.LearningVectorEntity;
import com.achobeta.domain.rag.model.valobj.LearningStatisticsVO;

import java.util.List;
import java.util.Map;

/**
 * @Auth : Malog
 * @Desc : 学习数据仓储接口
 * @Time : 2025/11/25
 */
public interface ILearningDataRepository {
    
    /**
     * 获取用户最近N天的学习向量数据
     */
    List<LearningVectorEntity> getUserRecentLearningData(String userId, int days);
    
    /**
     * 获取用户最近N天的学习统计数据
     */
    LearningStatisticsVO getUserLearningStatistics(String userId, int days);
    
    /**
     * 获取用户最近N天按科目分组的学习数据
     */
    List<Map<String, Object>> getUserLearningDataBySubject(String userId, int days);
    
    /**
     * 获取用户最近N天按行为类型分组的学习数据
     */
    List<Map<String, Object>> getUserLearningDataByActionType(String userId, int days);
}
