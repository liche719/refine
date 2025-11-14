package com.achobeta.trigger.http;


import com.achobeta.api.dto.StudyOverviewDTO;
import com.achobeta.domain.overview.service.ILearningOverviewService;
import com.achobeta.domain.overview.model.valobj.StudyOverviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学习概览接口
 */
@Slf4j
@Validated
@CrossOrigin("${app.config.cross-origin}:*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/${app.config.api-version}/overview")
public class LearningOverviewController {
    private final ILearningOverviewService service;
    @GetMapping("/get_overview")
    public StudyOverviewDTO getOverview(@Param("userId") int userId) {
        try {
            log.info("用户获取学习概览，userId:{}", userId);
            StudyOverviewVO vo = service.getOverview(userId);
            return StudyOverviewDTO.builder()
                    .questionsNum(vo.getQuestionsNum())
                    .reviewRate(vo.getReviewRate())
                    .hardQuestions(vo.getHardQuestions())
                    .studyTime(vo.getStudyTime())
                    .build();
        } catch (Exception e) {
            log.error("getOverview error", e);
            return null;
        }
    }
}
