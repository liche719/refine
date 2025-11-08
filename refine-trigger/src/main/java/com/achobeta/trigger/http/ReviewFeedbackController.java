package com.achobeta.trigger.http;


import com.achobeta.api.dto.TrickyKnowledgePointDTO;
import com.achobeta.api.dto.OverdueReviewDTO;
import com.achobeta.api.dto.StatsDTO;
import com.achobeta.domain.Feetback.model.entity.MistakeQuestionEntity;
import com.achobeta.domain.Feetback.model.valobj.TrickyKnowledgePointVO;
import com.achobeta.domain.Feetback.model.valobj.MistakeQueryParamsVO;
import com.achobeta.domain.Feetback.model.valobj.OverdueCountVO;
import com.achobeta.domain.Feetback.model.valobj.StatsVO;
import com.achobeta.domain.Feetback.service.feedback.IReviewFeedbackService;
import com.achobeta.types.Response;
import com.achobeta.types.enums.TimeRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 待复习题目反馈接口
 */
@Slf4j
@Validated
@CrossOrigin("${app.config.cross-origin}:*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/feedback/review")
public class ReviewFeedbackController {


    private final IReviewFeedbackService reviewFeedbackService;

    /**
     * 获取用户超过一周未复习题目数量
     * @param userId
     * @return
     */
    @GetMapping("/overdue-count")
    public Response<OverdueReviewDTO> getOverdueReviewCount(@RequestParam int userId) {
        try{
            log.info("用户获取待复习题目数量开始，userId:{}", userId);
            OverdueCountVO overdueCountVO = reviewFeedbackService.getOverdueCount(userId);
            log.info("用户获取待复习题目数量结束，userId:{} count:{}", userId, overdueCountVO.getCount());

            return Response.SYSTEM_SUCCESS(OverdueReviewDTO.builder()
                    .count(overdueCountVO.getCount())
                    .description(overdueCountVO.getDescription())
                    .build());
        }catch (Exception e){
            log.error("用户获取待复习题目数量失败！userId:{}", userId, e);
            return Response.SERVICE_ERROR(e.getMessage());
        }
    }

    /**
     * 获取用户近期(一周)出错多(>= 3)的知识点
     */
    @GetMapping("/tricky_knowledge")
    public Response<List<TrickyKnowledgePointDTO>> getTrickyKnowledgePoint(@RequestParam int userId){
        try{
            log.info("用户获取近期出错最多的知识点，userId:{}", userId);
            List<TrickyKnowledgePointVO> trickyKnowledgePointVOS = reviewFeedbackService.getTrickyKnowledgePoint(userId);
            List<TrickyKnowledgePointDTO> trickyKnowledgePointDTOS = trickyKnowledgePointVOS.stream()
                    .map(trickyKnowledgePointVO -> TrickyKnowledgePointDTO.builder()
                            .knowledgeId(trickyKnowledgePointVO.getKnowledgeId())
                            .knowledgeName(trickyKnowledgePointVO.getKnowledgeName())
                            .build()).toList();
            return Response.SYSTEM_SUCCESS(trickyKnowledgePointDTOS);
        }catch (Exception e){
            log.error("用户获取近期出错最多的知识点失败！userId:{}", userId, e);
            return Response.SERVICE_ERROR(e.getMessage());
        }
    }

    /**
     * 获取用户待复习题目列表
     */
    @GetMapping("/list")
    public ResponseEntity<Page<MistakeQuestionEntity>> list(
            @RequestParam int userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> subject,
            @RequestParam(required = false) List<String> errorType,
            @RequestParam(required = false) String timeRange,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            MistakeQueryParamsVO params = new MistakeQueryParamsVO();
            params.setUserId(userId);
            params.setKeyword(keyword);
            params.setSubject(subject);
            params.setErrorType(errorType);
            params.setTimeRange(TimeRange.valueOf(timeRange.toUpperCase()));
            params.setPage(page);
            params.setSize(size);

            log.info("用户筛选获取待复习题目列表开始，userId:{}", userId);
            Page<MistakeQuestionEntity> result = reviewFeedbackService.searchAndFilter(params);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.error("用户筛选获取待复习题目列表失败！userId:{}", userId, e);
            return null;
        }
    }

    @DeleteMapping("/deleteBatch")
    public ResponseEntity<String> deleteBatch(@RequestParam int userId, @RequestParam List<Integer> questionIds) {
        try {
            log.info("用户删除待复习题目开始，userId:{}", userId);
            reviewFeedbackService.deleteBatch(userId, questionIds);
            log.info("用户删除待复习题目结束，userId:{}", userId);
            return ResponseEntity.ok("删除成功");
        } catch (Exception e) {
            log.error("用户删除待复习题目失败！userId:{}", userId, e);
            return ResponseEntity.status(500).body("删除失败");
        }
    }

    /**
     * 获取待复习题目统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<StatsDTO> getStatistics(@RequestParam int userId) {
        try {
            log.info("获取待复习题目统计信息开始");
            StatsVO statsVO = reviewFeedbackService.getStatistics(userId);
            StatsDTO statsDTO = StatsDTO.builder()
                    .subjectDistribution(statsVO.getSubjectDistribution().stream().map(count -> {
                        Map<String, Integer> map = new HashMap<>();
                        map.put(count.getName(), count.getCount());
                        return map;
                    }).collect(Collectors.toList()))
                    .knowledgeDistribution(statsVO.getKnowledgeDistribution().stream().map(count -> {
                        Map<String, Integer> map = new HashMap<>();
                        map.put(count.getName(), count.getCount());
                        return map;
                    }).collect(Collectors.toList()))
                    .build();
            return ResponseEntity.ok(statsDTO);
        } catch (Exception e) {
            log.error("获取待复习题目统计信息失败！", e);
            throw new RuntimeException(e);
        }
    }

}
