package com.achobeta.trigger.http;


import com.achobeta.api.dto.*;
import com.achobeta.domain.IRedisService;
import com.achobeta.domain.keypoints_explanation.model.valobj.*;
import com.achobeta.domain.keypoints_explanation.service.IKeyPointsExplanationService;
import com.achobeta.types.annotation.GlobalInterception;
import com.achobeta.types.common.Constants;
import com.achobeta.types.common.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 知识点解释接口
 */
@Slf4j
@Validated
@CrossOrigin("${app.config.cross-origin}:*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/${app.config.api-version}/keypoints_explanation")
public class KeyPointsExplanationController {
    private final IKeyPointsExplanationService keyPointsExplanationService;

    /**
     * 根据学科获取中心知识点
     * @param subject
     * @return
     */
    @GetMapping("/get_key_points")
    @GlobalInterception
    public ResponseEntity<List<KeyPointsDTO>> getKeyPoints(@Param("subject") String subject) {
        String userId = UserContext.getUserId();
        log.info("用户获取中心知识点，subject:{}", subject);
        List<KeyPointsVO> keyPoints = keyPointsExplanationService.getKeyPoints(subject, userId);
        if (keyPoints == null || keyPoints.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(keyPoints.stream()
                .map(keyPointsVO -> KeyPointsDTO.builder()
                        .id(keyPointsVO.getId())
                        .keyPoints(keyPointsVO.getKeyPoints())
                        .build())
                .toList());
    }
    /**
     * 获取子知识点
     * @param knowledgeId 父知识点id
     * @return
     */
    @GetMapping("/get_son_key_points")
    @GlobalInterception
    public List<KeyPointsDTO> getSonKeyPoints(@Param("knowledgeId") int knowledgeId) {
        String userId = UserContext.getUserId();

        log.info("用户获取子知识点，knowledgeId:{}, userId:{}", knowledgeId, userId);
        List<KeyPointsVO> keyPointsVOs = keyPointsExplanationService.getSonKeyPoints(knowledgeId, userId);
        //空值判断
        if (keyPointsVOs == null || keyPointsVOs.isEmpty()) {
            return new ArrayList<>();
        }
        return keyPointsVOs.stream()
                .map(keyPointsVO -> KeyPointsDTO.builder()
                        .id(keyPointsVO.getId())
                        .keyPoints(keyPointsVO.getKeyPoints())
                        .build())
                .toList();
    }

    /**
     * 获取知识点详情
     */
    @GetMapping("/{knowledgeId}")
    @GlobalInterception
    public ResponseEntity<String> getKnowledgePoint(@PathVariable int knowledgeId ) {
        String userId = UserContext.getUserId();
        String point = keyPointsExplanationService.getKnowledgedescById(knowledgeId, userId);
        if (point == null || point.isEmpty()) {
            return ResponseEntity.ok("暂无知识点详情");
        }
        return ResponseEntity.ok(point);
    }

    /**
     * 获取相关错题统计数量
     * todo 完善学习反馈模块获取错题内容
     */
    @GetMapping("/{knowledgeId}/related-questions-statistic")
    @GlobalInterception
    public ResponseEntity<String> getRelatedWrongQuestionsStatistic(
            @PathVariable int knowledgeId ) {
        String userId = UserContext.getUserId();

        WrongQuestionVO questions = keyPointsExplanationService.getRelatedWrongQuestionsStatistic(knowledgeId, userId);
        if(questions.getUpdateCount() == 0){
            String res = "暂无相关错题";
            return ResponseEntity.ok(res);
        }
        String res = "该知识点上传了" + questions.getUpdateCount() + "道题，其中本周复习了" + questions.getReviewCount() + "道题";
        return ResponseEntity.ok(res);
    }

    /**
     * 获取相关错题或笔记
     *
     */
    @GetMapping("/{knowledgeId}/related-questions")
    @GlobalInterception
    public ResponseEntity<RelateQeustionDTO> getRelatedWrongQuestions(
            @PathVariable int knowledgeId ) {
        String userId = UserContext.getUserId();
        RelateQuestionVO relatedQuestions = keyPointsExplanationService.getRelatedWrongQuestions(knowledgeId, userId);
        if (relatedQuestions == null){
            return ResponseEntity.ok(RelateQeustionDTO.builder()
                    .questions(new ArrayList<>())
                    .note("")
                    .build());
        }
        return ResponseEntity.ok(RelateQeustionDTO.builder()
                .questions(relatedQuestions.getQestions().stream()
                        .map(questionVO -> QuestionDTO.builder()
                                .id(questionVO.getId())
                                .question(questionVO.getQuestion())
                                .build())
                        .toList())
                .note(relatedQuestions.getNote())
                .build());
    }

    /**
     * 标记知识点已掌握
     */
    @PostMapping("/{knowledgeId}/mark-as-mastered")
    @GlobalInterception
    public ResponseEntity<String> markAsMastered(@PathVariable int knowledgeId ) {
        String userId = UserContext.getUserId();
        keyPointsExplanationService.markAsMastered(knowledgeId, userId);
        return ResponseEntity.ok("已修改成功");
    }

    /**
     * 获取相关知识点
     */
    @GetMapping("/{knowledgeId}/related-points")
    @GlobalInterception
    public ResponseEntity<List<KeyPointsDTO>> getRelatedKnowledgePoints(
            @PathVariable int knowledgeId ) {
        String userId = UserContext.getUserId();

        List<KeyPointsVO> relatedPoints = keyPointsExplanationService.getRelatedKnowledgePoints(knowledgeId, userId);
        if (relatedPoints == null || relatedPoints.isEmpty()){
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(relatedPoints.stream()
                .map(relatedPoint -> KeyPointsDTO.builder()
                        .id(relatedPoint.getId())
                        .keyPoints(relatedPoint.getKeyPoints())
                        .build())
                .toList());
    }

    /**
     * 保存或更新学生笔记
     */
    @PostMapping("/{knowledgeId}/notes")
    @GlobalInterception
    public ResponseEntity<String> saveOrUpdateStudentNote(@PathVariable int knowledgeId, @RequestBody String note ) {
        String userId = UserContext.getUserId();
        keyPointsExplanationService.savedNote(note, knowledgeId, userId);
        return ResponseEntity.ok("笔记更新成功");
    }

    /**
     * 节点重命名
     */
    @PostMapping("/{knowledgeId}/rename")
    @GlobalInterception
    public ResponseEntity<String> renameNode(@PathVariable int knowledgeId, @RequestBody String newName ) {
        String userId = UserContext.getUserId();
        keyPointsExplanationService.renameNode(knowledgeId, newName, userId);
        return ResponseEntity.ok("重命名成功");
    }

    /**
     * 显示tooltip
     */
    @GetMapping("/{knowledgeId}/show-tooltip")
    @GlobalInterception
    public ResponseEntity<?> showTooltip(@PathVariable int knowledgeId ) {
        String userId = UserContext.getUserId();
        ToolTipVO tooltip = keyPointsExplanationService.gettooltipById(knowledgeId, userId);
        if(tooltip == null || tooltip.getTotal() == 0){
            ToolTipDTO tooltipDTO = ToolTipDTO.builder()
                    .count(0)
                    .lastReviewTime("")
                    .degreeOfProficiency(0)
                    .build();
            return ResponseEntity.ok(tooltipDTO);
        }
        double degreeOfProficiency = 1.0 * tooltip.getCount() / tooltip.getTotal();
        return ResponseEntity.ok(ToolTipDTO.builder()
                .count(tooltip.getCount())
                .lastReviewTime(tooltip.getLastReviewTime())
                .degreeOfProficiency(degreeOfProficiency)
                .build());
    }

    /**
     * 添加子知识点
     */
    @PostMapping("/{knowledgeId}/add-son-point")
    @GlobalInterception
    public ResponseEntity<String> addSonPoint(@PathVariable String knowledgeId, @RequestBody SonPointVO sonPoints ) {
        String userId = UserContext.getUserId();
        keyPointsExplanationService.addSonPoint(sonPoints, userId, knowledgeId);
        return ResponseEntity.ok("添加成功");
    }

}
