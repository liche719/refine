package com.achobeta.trigger.http;


import com.achobeta.api.dto.StudyOverviewDTO;
import com.achobeta.domain.IRedisService;
import com.achobeta.domain.overview.model.valobj.LearningDynamicVO;
import com.achobeta.domain.overview.service.ILearningOverviewService;
import com.achobeta.domain.overview.model.valobj.StudyOverviewVO;
import com.achobeta.domain.rag.service.ILearningDynamicsService;
import com.achobeta.types.Response;
import com.achobeta.types.annotation.GlobalInterception;
import com.achobeta.types.common.Constants;
import com.achobeta.types.common.UserContext;
import com.achobeta.types.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.achobeta.types.enums.GlobalServiceStatusCode.*;

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
    private final ILearningDynamicsService learningDynamicsService;

    /**
     * 获取学习概览
     * @return 学习概览
     */
    @GetMapping("/get_overview")
    @GlobalInterception
    public Response<StudyOverviewDTO> getOverview() {
        String userId = UserContext.getUserId();
        StudyOverviewVO vo = null;
        try {
            log.info("用户获取学习概览，userId:{}", userId);
            vo = service.getOverview(userId);
        } catch (Exception e) {
            return Response.CUSTOMIZE_ERROR(REQUEST_NOT_VALID);
        }
        return Response.SYSTEM_SUCCESS(StudyOverviewDTO.builder()
                .questionsNum(vo.getQuestionsNum())
                .reviewRate(vo.getReviewRate())
                .hardQuestions(vo.getHardQuestions())
                .studyTime(vo.getStudyTime())
                .build());

    }

    /**
     * 获取学习动态
     * @return 学习动态描述列表
     */
    @GetMapping("/get_study_dynamic")
    @GlobalInterception
    public Response<List<String>> getStudyDynamic(){
        String userId = UserContext.getUserId();
        List<String> result = null;
        try {
            log.info("用户获取学习动态，userId:{}", userId);
            
            // 获取学习动态数据
            List<com.achobeta.domain.rag.model.valobj.LearningDynamicVO> dynamics = 
                    learningDynamicsService.analyzeUserLearningDynamics(userId);
            
            log.info("获取到学习动态数量: {}", dynamics != null ? dynamics.size() : 0);
            
            if (dynamics == null || dynamics.isEmpty()) {
                log.warn("未获取到学习动态数据，userId: {}", userId);
                return Response.SYSTEM_SUCCESS(java.util.Collections.emptyList());
            }
            
            // 提取描述信息
            result = dynamics.stream()
                    .map(dynamic -> {
                        log.info("处理学习动态: type={}, title={}, description={}", 
                                dynamic.getType(), dynamic.getTitle(), dynamic.getDescription());
                        return dynamic.getDescription();
                    })
                    .filter(description -> description != null && !description.trim().isEmpty())
                    .distinct() // 去重，防止重复的描述
                    .collect(java.util.stream.Collectors.toList());
                    
            log.info("最终返回描述数量: {}", result.size());
            
            // 打印每个描述用于调试
            for (int i = 0; i < result.size(); i++) {
                log.info("描述[{}]: {}", i, result.get(i));
            }
            
        } catch (Exception e) {
            log.error("getStudyDynamic error", e);
            return Response.CUSTOMIZE_ERROR(GET_STUDY_DYNAMIC_FAIL);
        }
        return Response.SYSTEM_SUCCESS(result);
    }
}
