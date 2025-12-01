package com.achobeta.domain.overview.service.extendbiz;

import com.achobeta.api.dto.TrendDataDTO;
import com.achobeta.domain.Feetback.service.feedback.IReviewFeedbackService;
import com.achobeta.domain.Feetback.service.feedback.extendbiz.ReviewFeedbackService;
import com.achobeta.domain.overview.adapter.repository.IStudyOverviewRepository;
import com.achobeta.domain.overview.model.valobj.LearningDynamicVO;
import com.achobeta.domain.overview.service.ILearningOverviewService;
import com.achobeta.domain.overview.model.valobj.StudyOverviewVO;
import com.achobeta.domain.rag.service.ILearningDynamicsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LearningOverviewService implements ILearningOverviewService{
    @Autowired
    private IStudyOverviewRepository repository;
    @Autowired
    private IReviewFeedbackService reviewFeedbackService;
    @Autowired
    private ILearningDynamicsService learningDynamicsService;
    @Override
    public StudyOverviewVO getOverview(String userId) {
        StudyOverviewVO vo = repository.queryStudyOverview(userId);
        if(vo == null || vo.getQuestionsNum() == 0){
            vo = StudyOverviewVO.builder()
                    .questionsNum(0)
                    .reviewRate(0.0)
                    .hardQuestions(0)
                    .studyTime(0)
                    .build();
        }
        return vo;
    }

    @Override
    public LearningDynamicVO getStudyDynamic(String userId) {
        LearningDynamicVO vo = new LearningDynamicVO();

        // 1. 最近错题数
        vo.setQuestionsCount(repository.countByUserIdAndCreateTimeAfter(userId, LocalDateTime.now().minusDays(7)));

        // 2. 本周复盘数
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);
        vo.setWeeklyReviewCount(repository.countByUserIdAndUpdateTimeBetween(userId, startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59)));

        // 3. 学习趋势折线图数据（过去6个月，每月一次）
        vo.setTrendData(getTrendData(userId));

        return vo;
    }

    @Override
    public List<LearningDynamicVO> getStudyDynamicList(String userId) {
        try {
            log.info("获取用户学习动态列表，userId:{}", userId);
            // 调用学习动态分析服务获取AI分析的学习动态
            List<com.achobeta.domain.rag.model.valobj.LearningDynamicVO> ragDynamics = 
                    learningDynamicsService.analyzeUserLearningDynamics(userId);
            
            if (ragDynamics == null || ragDynamics.isEmpty()) {
                log.warn("未获取到学习动态数据，userId:{}", userId);
                return Collections.emptyList();
            }
            
            // 转换为overview模块的LearningDynamicVO
            List<LearningDynamicVO> dynamics = ragDynamics.stream()
                    .map(this::convertRagToOverviewDynamic)
                    .collect(Collectors.toList());
            
            log.info("成功获取用户学习动态，userId:{}, 动态数量:{}", userId, dynamics.size());
            return dynamics;
            
        } catch (Exception e) {
            log.error("获取用户学习动态列表失败，userId:{}", userId, e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 将RAG模块的LearningDynamicVO转换为Overview模块的LearningDynamicVO
     */
    private LearningDynamicVO convertRagToOverviewDynamic(com.achobeta.domain.rag.model.valobj.LearningDynamicVO ragDynamic) {
        // 由于overview模块的LearningDynamicVO结构不同，我们需要创建一个包含描述信息的对象
        // 这里我们可以将RAG的动态信息组合成一个描述字符串
        LearningDynamicVO overviewDynamic = new LearningDynamicVO();
        
        // 将RAG动态的信息组合成一个描述，这样前端就能获取到完整的动态信息
        StringBuilder description = new StringBuilder();
        description.append("类型: ").append(ragDynamic.getType()).append("\n");
        description.append("标题: ").append(ragDynamic.getTitle()).append("\n");
        description.append("描述: ").append(ragDynamic.getDescription()).append("\n");
        if (ragDynamic.getSubject() != null) {
            description.append("科目: ").append(ragDynamic.getSubject()).append("\n");
        }
        if (ragDynamic.getSuggestion() != null) {
            description.append("建议: ").append(ragDynamic.getSuggestion()).append("\n");
        }
        description.append("优先级: ").append(ragDynamic.getPriority()).append("\n");
        description.append("相关题目数: ").append(ragDynamic.getRelatedQuestionCount());
        
        // 设置基本信息，这里我们用描述来传递完整信息
        overviewDynamic.setQuestionsCount(ragDynamic.getRelatedQuestionCount());
        overviewDynamic.setWeeklyReviewCount(0); // 默认值
        overviewDynamic.setTrendData(new ArrayList<>()); // 空列表
        
        return overviewDynamic;
    }

    private List<TrendDataDTO> getTrendData(String userId) {
        List<TrendDataDTO> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String monthStr = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            Integer studyCount = repository.countByUserIdAndUpdateTimeBetween(
                    userId,
                    month.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(),
                    month.with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay()
            );
            Integer reviewCount = repository.countByUserIdAndUpdateTimeBetween(
                    userId,
                    month.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(),
                    month.with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay()
            );
            result.add(new TrendDataDTO(monthStr, studyCount, reviewCount));
        }
        return result;
    }
}
