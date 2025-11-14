package com.achobeta.trigger.http;

import com.achobeta.api.dto.question.QuestionResponseDTO;
import com.achobeta.domain.IRedisService;
import com.achobeta.domain.question.service.impl.QuestionServiceImpl;
import com.achobeta.types.Response;
import com.achobeta.types.annotation.GlobalInterception;
import com.achobeta.types.common.Constants;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.achobeta.types.common.Constants.QUESTION_GENERATION_ID_KEY;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionServiceImpl questionService;

    private final IRedisService redis;


    /*
        生成出来的题目先放入redis，
        如果用户答案正确，则直接移除题目；否则录入错题数据库，再移除redis
     */


    /**
     * 题目生成，目前支持判分的只有填空题和选择题
     * @return  题目id，题目内容（含答案）
     */
    @GlobalInterception
    @PostMapping("/generation")
    public Response<QuestionResponseDTO> questionGeneration(@RequestHeader("token") String token, @NotNull Integer mistakeQuestionId) {
        try {
            String userId = redis.getValue(Constants.USER_ID_KEY_PREFIX + token);
            QuestionResponseDTO responseDTO = questionService.questionGeneration(userId, mistakeQuestionId);
            return Response.SYSTEM_SUCCESS(responseDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 题目回答错误时调用，存入用户错题数据库
     */
    @GlobalInterception
    @PostMapping("/handle/mistakequestion")
    public Response handleWrongQuestion(@RequestHeader("token") String token, String questionId) {
        try {
            String userId = redis.getValue(Constants.USER_ID_KEY_PREFIX + token);
            log.info("用户 {} 录入错题中，错题id: {}", userId, questionId);
            questionService.recordMistakeQuestion(userId, questionId);
            return Response.SYSTEM_SUCCESS("该题已录入错题");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            redis.remove(QUESTION_GENERATION_ID_KEY + questionId);
            log.info("已删除redis题目缓存，题目id：{}", questionId);
        }
    }



}