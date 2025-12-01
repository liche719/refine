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
    private final IRedisService redisService;

    /**
     * 获取学习概览
     *
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
     *
     * @return 学习动态描述列表
     */
    @GetMapping("/get_study_dynamic")
    @GlobalInterception
    public Response<List<String>> getStudyDynamic() {
        String userId = UserContext.getUserId();
        List<String> result = null;
        try {
            log.info("用户获取学习动态，userId:{}", userId);

            // 优先从缓存获取数据
            String cacheKey = "user_dynamics:" + userId;
            log.info("尝试从缓存获取数据，cacheKey: {}", cacheKey);
            
            Object cachedData = redisService.getValue(cacheKey);
            log.info("缓存查询结果: {}", cachedData != null ? "有数据" : "无数据");

            if (cachedData != null) {
                log.info("从缓存获取学习动态，userId: {}, 数据类型: {}", userId, cachedData.getClass().getSimpleName());
                try {
                    if (cachedData instanceof List) {
                        result = (List<String>) cachedData;
                        log.info("缓存命中，返回{}条学习动态描述", result.size());
                        return Response.SYSTEM_SUCCESS(result);
                    } else if (cachedData instanceof String) {
                        // 如果是字符串，尝试解析为JSON
                        String jsonStr = (String) cachedData;
                        log.info("缓存数据为字符串，尝试解析JSON: {}", jsonStr);
                        // 这里可以添加JSON解析逻辑
                    } else {
                        log.warn("缓存数据类型不匹配，期望List，实际: {}, 数据内容: {}", 
                                cachedData.getClass().getSimpleName(), cachedData);
                    }
                } catch (Exception e) {
                    log.error("处理缓存数据失败", e);
                }
            }

            log.info("缓存未命中，实时分析学习动态，userId: {}", userId);

            // 缓存未命中，实时获取学习动态数据
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
                        log.debug("处理学习动态: type={}, title={}, description={}",
                                dynamic.getType(), dynamic.getTitle(), dynamic.getDescription());
                        return dynamic.getDescription();
                    })
                    .filter(description -> description != null && !description.trim().isEmpty())
                    .distinct() // 去重，防止重复的描述
                    .collect(java.util.stream.Collectors.toList());

            log.info("最终返回描述数量: {}", result.size());

            // 将结果缓存起来，缓存30分钟 (1800秒 = 30分钟)
            try {
                log.info("准备缓存学习动态结果，cacheKey: {}, 数据: {}", cacheKey, result);
                redisService.setValue(cacheKey, result, 1800 * 1000);
                log.info("学习动态结果已缓存，userId: {}, 缓存时间: 30分钟", userId);
                
                // 验证缓存是否成功
                Object verifyCache = redisService.getValue(cacheKey);
                log.info("缓存验证结果: {}", verifyCache != null ? "缓存成功" : "缓存失败");
            } catch (Exception e) {
                log.error("缓存学习动态结果失败，userId: {}", userId, e);
            }

        } catch (Exception e) {
            log.error("getStudyDynamic error", e);
            return Response.CUSTOMIZE_ERROR(GET_STUDY_DYNAMIC_FAIL);
        }
        return Response.SYSTEM_SUCCESS(result);
    }

}
