package com.achobeta.domain.overview.service.extendbiz;

import com.achobeta.domain.overview.adapter.repository.IStudyOverviewRepository;
import com.achobeta.domain.overview.service.ILearningOverviewService;
import com.achobeta.domain.overview.model.valobj.StudyOverviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LearningOverviewService implements ILearningOverviewService{
    @Autowired
    private IStudyOverviewRepository repository;
    @Override
    public StudyOverviewVO getOverview(int userId) {
        return repository.queryStudyOverview(userId);
    }
}
