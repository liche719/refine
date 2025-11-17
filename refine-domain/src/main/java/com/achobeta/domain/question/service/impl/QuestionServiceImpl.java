package com.achobeta.domain.question.service.impl;

import com.achobeta.domain.question.adapter.port.AiGenerationService;
import com.achobeta.domain.question.adapter.repository.IKnowledgeRepository;
import com.achobeta.domain.question.adapter.repository.IMistakeRepository;
import com.achobeta.domain.question.model.entity.MistakeQuestionEntity;
import com.achobeta.domain.question.model.po.MistakeKnowledgePO;
import com.achobeta.api.dto.MistakeQuestionDTO;
import com.achobeta.api.dto.QuestionResponseDTO;
import com.achobeta.domain.question.service.IQuestionService;
import com.achobeta.types.enums.GlobalServiceStatusCode;
import com.achobeta.types.exception.AppException;
import com.achobeta.types.support.postprocessor.AbstractPostProcessor;
import com.achobeta.types.support.postprocessor.PostContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

import static com.achobeta.types.common.Constants.QUESTION_GENERATION_EXPIRED_SECONDS;
import static com.achobeta.types.common.Constants.QUESTION_GENERATION_ID_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl extends AbstractPostProcessor<QuestionResponseDTO> implements IQuestionService {

    private final IMistakeRepository mistakeRepository;

    private final IKnowledgeRepository knowledgeRepository;

    private final AiGenerationService aiGenerationService;


    /**
     * 题目生成
     */
    public QuestionResponseDTO questionGeneration(String userId, Integer mistakeQuestionId) {

        MistakeKnowledgePO po = mistakeRepository.findSubjectAndKnowledgeIdById(mistakeQuestionId);
        String subject = po.getSubject();
        Integer knowledgeId = po.getKnowledgeId();
        String knowledgePointName = knowledgeRepository.findKnowledgeNameById(po.getKnowledgeId());

        String toAi = "你是一位" + subject + "老师，请根据\"" + knowledgePointName + "\"知识点出一道题目";

        // 调用外部接口生成新题目
        QuestionResponseDTO question = aiGenerationService.Generation(toAi);
        if (null == question.getContent() || null == question.getAnswer()) {
            throw new AppException(GlobalServiceStatusCode.QUESTION_GENERATION_FAIL);
        }

        // 构建错题实体
        String questionId = String.valueOf(mistakeQuestionId + System.currentTimeMillis());

        MistakeQuestionDTO dto = new MistakeQuestionDTO();
        dto.setUserId(userId);
        dto.setQuestionId(questionId);
        dto.setQuestionContent(question.getContent());
        dto.setAnswer(question.getAnswer());
        dto.setSubject(subject);
        dto.setKnowledgePointId(knowledgeId);

        // 暂存题目到redis
        mistakeRepository.setValue(QUESTION_GENERATION_ID_KEY + questionId, dto, QUESTION_GENERATION_EXPIRED_SECONDS);

        return QuestionResponseDTO.builder()
                .questionId(questionId)
                .content(question.getContent())
                .build();
    }

    /**
     * 把redis的错题存入数据库
     */
    @Override
    public void recordMistakeQuestion(String userId, String questionId) {

        MistakeQuestionDTO vo = mistakeRepository.getValue(QUESTION_GENERATION_ID_KEY + questionId);
        if (null == vo) {
            throw new AppException(GlobalServiceStatusCode.QUESTION_IS_EXPIRED);
        }
        if (!userId.equals(vo.getUserId())) {
            throw new AppException(GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        mistakeRepository.save(MistakeQuestionEntity.builder()
                .userId(userId)
                .questionContent(vo.getQuestionContent())
                .subject(vo.getSubject())
                .knowledgePointId(vo.getKnowledgePointId())
                .questionStatus(0)
                .createTime(LocalDateTime.now())
                .build());

        this.removeQuestionCache(questionId);
    }

    @Override
    public PostContext<QuestionResponseDTO> doMainProcessor(PostContext<QuestionResponseDTO> postContext) {
        return null;
    }

    public void removeQuestionCache(String questionId) {
        mistakeRepository.remove(QUESTION_GENERATION_ID_KEY + questionId);
        log.info("已删除redis题目缓存，题目id：{}", questionId);
    }

    public Flux<ServerSentEvent<String>> aiJudge(String questionId, String answer) {
        MistakeQuestionDTO value = mistakeRepository.getValue(QUESTION_GENERATION_ID_KEY + questionId);
        String questionContent = value.getQuestionContent();
        String chat = "请判断\"" + questionContent + "\"的答案:" + answer + "是否正确，并给出解析。";

        return aiGenerationService.aiJudgeStream(chat)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

}