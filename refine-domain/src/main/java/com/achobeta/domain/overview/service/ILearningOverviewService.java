package com.achobeta.domain.overview.service;

import com.achobeta.domain.overview.model.valobj.LearningDynamicVO;
import com.achobeta.domain.overview.model.valobj.StudyOverviewVO;

public interface ILearningOverviewService {
    /**
     * 获取学习概览
     *
     * @param userId 用户id
     * @return 学习概览
     */
    StudyOverviewVO getOverview(String userId);

    /**
     * 获取学习动态
     *
     * @param userId 用户id
     * @return 学习动态
     */
    LearningDynamicVO getStudyDynamic(String userId);
}
