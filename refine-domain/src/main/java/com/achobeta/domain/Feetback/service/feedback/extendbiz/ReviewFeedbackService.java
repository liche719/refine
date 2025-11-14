package com.achobeta.domain.Feetback.service.feedback.extendbiz;

import com.achobeta.domain.Feetback.adapter.repository.ErrorTypeMapper;
import com.achobeta.domain.Feetback.adapter.repository.IReviewQuestionRepository;
import com.achobeta.domain.Feetback.adapter.repository.ITrickyKnowledgeRepository;
import com.achobeta.domain.Feetback.adapter.repository.MistakeQuestionMapper;
import com.achobeta.domain.Feetback.model.entity.MistakeQuestionEntity;
import com.achobeta.domain.Feetback.model.valobj.*;
import com.achobeta.domain.Feetback.service.feedback.IReviewFeedbackService;
import com.achobeta.types.enums.TimeRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.achobeta.types.enums.TimeRange.MORE_THAN_ONE_WEEK;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewFeedbackService implements IReviewFeedbackService {
    private final IReviewQuestionRepository repository;
    private final ITrickyKnowledgeRepository trickyKnowledgeRepository;

    @Autowired
    private MistakeQuestionMapper mapper;


    @Override
    public OverdueCountVO getOverdueCount(int userId) {
        return OverdueCountVO.builder()
                .count(repository.queryReviewQuestions(userId))
                .description("待复习题目数量")
                .build();
    }

    @Override
    public Page<MistakeQuestionEntity> searchAndFilter(MistakeQueryParamsVO params) {
        // 构造查询条件
        int userId = params.getUserId();
        String keyword = params.getKeyword();
        List<String> subject = params.getSubject();
        List<String> errorType = params.getErrorType();
        TimeRange timeRange = params.getTimeRange();

        // 如果选中时间，则计算时间范围
        LocalDate startDate = null;
        LocalDate endDate = null;
        if (timeRange != MORE_THAN_ONE_WEEK) {
            switch (timeRange) {
                case THIS_WEEK:
                    startDate = LocalDate.now().minusDays(7);
                    break;
                case THIS_MONTH:
                    startDate = LocalDate.now().withDayOfMonth(1);
                    break;
                case THIS_QUARTER:
                    startDate = LocalDate.now().minusMonths(3).withDayOfMonth(1);
                    break;
                case THIS_YEAR:
                    startDate = LocalDate.now().withDayOfYear(1);
                    break;
            }
        }else{
            //否则返回超过7天的题目
            endDate = LocalDate.now().minusDays(7);
        }

        // 处理错误类型
        List<String> errorTypeFields = new ArrayList<>();
        if (errorType != null && !errorType.isEmpty()) {
            for (String errorTy : errorType) {
                String field = ErrorTypeMapper.FIELD_MAP.get(errorTy);
                if (field != null) {
                    errorTypeFields.add(field);
                }
            }
        }
        // 调用 Mapper 查询
        List<MistakeQuestionEntity> list = mapper.selectByCondition(
                userId, keyword, subject, errorTypeFields, startDate, endDate
        );

        // 分页处理
        int pageNum = params.getPage() != 0 ? params.getPage() : 0;
        int pageSize = params.getSize() != 10 ? params.getSize() : 10;
        int start = pageNum * pageSize;
        int end = Math.min(start + pageSize, list.size());

        List<MistakeQuestionEntity> pageList = list.subList(start, end);

        return new PageImpl<>(pageList, PageRequest.of(pageNum, pageSize), list.size());
    }

    @Override
    public void deleteBatch(int userId, List<Integer> questionIds) {
        repository.deleteBatch(userId, questionIds);
    }

    @Override
    public StatsVO getStatistics(int userId) {
        // 时间范围：最近12个月
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(11).withDayOfMonth(1);

        StatsVO stats = new StatsVO();
        stats.setSubjectDistribution(repository.countBySubject(userId));
        stats.setKnowledgeDistribution(repository.countByKnowledge(userId));
        List<ReviewTrendVO> monthlyStats = repository.calculateReviewRate(userId, startDate, endDate);
        stats.setReviewTrend(monthlyStats);

        return stats;
    }

    /**
     * 获取用户近期出错最多的知识点
     * @param userId
     * @return
     */
    @Override
    public List<TrickyKnowledgePointVO> getTrickyKnowledgePoint(int userId) {
        return trickyKnowledgeRepository.getTrickyKnowledgePoints(userId);
    }

}
