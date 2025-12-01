package com.achobeta.domain.question.service.impl;

import cn.hutool.core.lang.UUID;
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
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.concurrent.Executor;

import static com.achobeta.types.common.Constants.QUESTION_GENERATION_EXPIRED_SECONDS;
import static com.achobeta.types.common.Constants.QUESTION_GENERATION_ID_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl extends AbstractPostProcessor<QuestionResponseDTO> implements IQuestionService {

    private final IMistakeRepository mistakeRepository;

    private final IKnowledgeRepository knowledgeRepository;

    private final AiGenerationService aiGenerationService;

    @Resource(name = "mistakeExecutor")
    private Executor mistakeExecutor;

    @Resource(name = "aiExclusiveThreadPool")
    private Executor aiExclusiveThreadPool;


    /**
     * 题目生成
     */
    public QuestionResponseDTO questionGeneration(String userId, String mistakeQuestionId) {

        MistakeKnowledgePO po = mistakeRepository.findSubjectAndKnowledgeIdById(mistakeQuestionId);
        String subject = po.getSubject();
        String knowledgeId = po.getKnowledgeId();
        String knowledgePointName = knowledgeRepository.findKnowledgeNameById(knowledgeId);

        if (null == subject) {
            throw new AppException("（ai出题）找不到题目所属科目,mistakeQuestionId" + mistakeQuestionId);
        }
        if (null == knowledgePointName) {
            throw new AppException("（ai出题）找不到该题知识点名称,mistakeQuestionId" + mistakeQuestionId);
        }

        // 调用外部接口生成新题目
        String toAi;
        QuestionResponseDTO question;
        if (subject.equals("未分类")) {
            toAi = "你是一位专业出题老师，请根据\"" + knowledgePointName + "\"知识点出一道题目";
            question = aiGenerationService.Generation(toAi);
        } else {
            toAi = "你是一位" + subject + "老师，请根据\"" + knowledgePointName + "\"知识点出一道题目";
            question = aiGenerationService.Generation(subject, toAi);
        }

        if (null == question.getContent() || null == question.getAnswer()) {
            throw new AppException(GlobalServiceStatusCode.QUESTION_GENERATION_FAIL);
        }

        // 构建错题实体
        String questionId = UUID.fastUUID().toString().substring(0, 19)+ System.currentTimeMillis();

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
        MistakeQuestionDTO value = mistakeRepository.getValue(QUESTION_GENERATION_ID_KEY + questionId);

        if (null == value) {
            throw new AppException(GlobalServiceStatusCode.QUESTION_IS_EXPIRED);
        }
        if (!userId.equals(value.getUserId())) {
            throw new AppException(GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }

        mistakeRepository.save(MistakeQuestionEntity.builder()
                .userId(userId)
                .questionId(questionId)
                .questionContent(value.getQuestionContent())
                .subject(value.getSubject())
                .knowledgePointId(value.getKnowledgePointId())
                .questionStatus(0)
                .createTime(LocalDateTime.now())
                .build());

        log.info("用户 {} 错题录入完成，题目id: {}", userId, questionId);

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

    public Flux<ServerSentEvent<String>> aiJudge(String userId, String questionId, String userAnswer) {
        MistakeQuestionDTO value = mistakeRepository.getValue(QUESTION_GENERATION_ID_KEY + questionId);

        if (null == value) {
            throw new AppException(GlobalServiceStatusCode.QUESTION_IS_EXPIRED);
        }
        if (!userId.equals(value.getUserId())) {
            throw new AppException(GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        String correctAnswer = value.getAnswer();

        Integer auto = 0;    // 用户可选是否自动录入TODO
        if (auto == 1) {
            autoRecord(userId, questionId, userAnswer, correctAnswer);
        }

        String questionContent = value.getQuestionContent();
        String chat = "题目原文：" + questionContent + "。"
                + "标准答案：" + correctAnswer + "。"
                + "用户提交的答案：" + userAnswer + "。"
                + "请严格按以下规则判题：1. 先对比用户答案与标准答案，完全一致（客观题全匹配/主观题核心得分点全中）则判定正确；部分一致（主观题部分得分点命中）则判定部分正确，明确正确与遗漏/错误点；完全不一致则判定错误，给出完整标准答案。"
                + "再按「题干拆解→考点关联→步骤化解题→答案验证」逻辑撰写详细解析，若用户答案错误/部分正确，需补充错误原因分析。"
                + "最后总结1-3个核心知识点及关键要点。"
                + "输出严格遵循以下要求：仅纯文本，分三段式（判题结论与标准答案、详细解析、知识点总结），解析步骤按「第一步、第二步」明确区分，无Markdown、列表符号及无关内容。";

        // 将ai流式调用提交到自定义线程池
        String subject = value.getSubject();
        if (subject.equals("未分类")) {
            return Flux.defer(() -> aiGenerationService.aiJudgeStream(chat))
                    .subscribeOn(Schedulers.fromExecutor(aiExclusiveThreadPool))
                    .doOnError(e -> log.error("流式聊天异常：用户Id={},题目id={}, 用户判题回答={}", userId, questionId, userAnswer, e))
                    .map(chunk -> ServerSentEvent.<String>builder()
                            .data(chunk)
                            .build());
        } else {
            return Flux.defer(() -> aiGenerationService.aiJudgeStream(subject, chat))
                    .subscribeOn(Schedulers.fromExecutor(aiExclusiveThreadPool))
                    .doOnError(e -> log.error("流式聊天异常：用户Id={},题目id={}, 用户判题回答={}", userId, questionId, userAnswer, e))
                    .map(chunk -> ServerSentEvent.<String>builder()
                            .data(chunk)
                            .build());
        }

    }

    @Override
    public String getQuestionKnowledge(String questionId) {
        return mistakeRepository.findKnowledgeNameById(questionId);
    }

    private void autoRecord(String userId, String questionId, String answer, String correctAnswer) {
        // 错误则异步录入错题，不阻塞主线程
        if (!correctAnswer.equalsIgnoreCase(answer.trim())) {
            log.info("用户 {} 答题错误，准备自动录入错题，题目id: {}", userId, questionId);
            // 提交到线程池异步执行
            mistakeExecutor.execute(() -> {
                try {
                    recordMistakeQuestion(userId, questionId);
                } catch (Exception e) {
                    log.error("用户 {} 错题异步录入失败，题目id: {}", userId, questionId, e);
                }
            });
        }
    }

}