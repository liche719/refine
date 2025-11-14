package com.achobeta.domain.overview.service;

import com.achobeta.domain.overview.model.valobj.StudyOverviewVO;

public interface ILearningOverviewService {
    /**
     * 获取学习概览
     *
     * @param userId 用户id
     * @return 学习概览
     */
    StudyOverviewVO getOverview(String userId);
}
