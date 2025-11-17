package com.achobeta.trigger.http;


import com.achobeta.api.dto.KeyPointsDTO;
import com.achobeta.api.dto.WrongQuestionDTO;
import com.achobeta.domain.IRedisService;
import com.achobeta.domain.keypoints_explanation.model.valobj.KeyPointsVO;
import com.achobeta.domain.keypoints_explanation.model.valobj.WrongQuestionVO;
import com.achobeta.domain.keypoints_explanation.service.IKeyPointsExplanationService;
import com.achobeta.types.common.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    private final IRedisService redis;

    /**
     * 根据学科获取中心知识点
     * @param subject
     * @return
     */
    @GetMapping("/get_key_points")
    public List<KeyPointsDTO> getKeyPoints(@Param("subject") String subject, @RequestHeader("token") String token) {
        String userId = redis.getValue(Constants.USER_ID_KEY_PREFIX + token);
        log.info("用户获取中心知识点，subject:{}", subject);
        return keyPointsExplanationService.getKeyPoints(subject, userId).stream()
                .map(keyPointsVO -> KeyPointsDTO.builder()
                        .id(keyPointsVO.getId())
                        .keyPoints(keyPointsVO.getKeyPoints())
                        .build())
                .toList();
    }
    /**
     * 获取子知识点
     * @param knowledgeId 父知识点id
     * @return
     */
    @GetMapping("/get_son_key_points")
    public List<KeyPointsDTO> getSonKeyPoints(@Param("knowledgeId") int knowledgeId, @RequestHeader("token") String token) {
        String userId = redis.getValue(Constants.USER_ID_KEY_PREFIX + token);
        log.info("用户获取子知识点，knowledgeId:{}, userId:{}", knowledgeId, userId);
        List<KeyPointsVO> keyPointsVOs = keyPointsExplanationService.getSonKeyPoints(knowledgeId, userId);
        //空值判断
        if (keyPointsVOs == null || keyPointsVOs.isEmpty()) {
            return null;
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
    public ResponseEntity<String> getKnowledgePoint(@PathVariable int knowledgeId, @RequestHeader("token") String token) {
        String userId = redis.getValue(Constants.USER_ID_KEY_PREFIX + token);
        String point = keyPointsExplanationService.getKnowledgedescById(knowledgeId, userId);
        if (point == null || point.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(point);
    }

    /**
     * 获取相关错题
     * todo: 需要额外获取错题id的集合，然后需要完善学习反馈模块获取错题内容
     */
    @GetMapping("/{knowledgeId}/related-questions")
    public ResponseEntity<String> getRelatedWrongQuestions(
            @PathVariable int knowledgeId, @RequestHeader("token") String token) {
        String userId = redis.getValue(Constants.USER_ID_KEY_PREFIX + token);
        WrongQuestionVO questions = keyPointsExplanationService.getRelatedWrongQuecstions(knowledgeId, userId);
        if(questions.getUpdateCount() == 0){
            String res = "暂无相关错题";
            return ResponseEntity.ok(res);
        }
        String res = "该知识点上传了" + questions.getUpdateCount() + "道题，其中本周复习了" + questions.getReviewCount() + "道题";
        return ResponseEntity.ok(res);
    }

    /**
     * 获取相关知识点
     */
    @GetMapping("/{knowledgeId}/related-points")
    public ResponseEntity<List<KeyPointsDTO>> getRelatedKnowledgePoints(
            @PathVariable int knowledgeId, @RequestHeader("token") String token) {
        String userId = redis.getValue(Constants.USER_ID_KEY_PREFIX + token);
        List<KeyPointsVO> relatedPoints = keyPointsExplanationService.getRelatedKnowledgePoints(knowledgeId, userId);
        if (relatedPoints == null || relatedPoints.isEmpty()){
            return ResponseEntity.notFound().build();
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
    @Scheduled(cron = "0 */5 * * * *")
    public ResponseEntity<Boolean> saveOrUpdateStudentNote(@PathVariable int knowledgeId, @RequestBody String note, @RequestHeader("token") String token) {
        String userId = redis.getValue(Constants.USER_ID_KEY_PREFIX + token);
        Boolean savedNote = keyPointsExplanationService.savedNote(note, knowledgeId, userId);
        return ResponseEntity.ok(savedNote);
    }

}
