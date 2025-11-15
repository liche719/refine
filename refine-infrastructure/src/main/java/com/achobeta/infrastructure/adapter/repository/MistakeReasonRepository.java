package com.achobeta.infrastructure.adapter.repository;

import com.achobeta.domain.mistake.adapter.repository.IMistakeReasonRepository;
import com.achobeta.domain.mistake.model.valobj.MistakeReasonVO;
import com.achobeta.infrastructure.dao.IMistakeQuestionDao;
import com.achobeta.infrastructure.dao.po.MistakeQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * @Auth : Malog
 * @Desc : 错因管理仓储实现
 * @Time : 2025/11/10
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MistakeReasonRepository implements IMistakeReasonRepository {

    private final IMistakeQuestionDao mistakeQuestionDao;

    @Override
    public MistakeReasonVO getMistakeReasons(String userId, String questionId) {
        try {
            if (userId == null || userId.isEmpty() || questionId == null || questionId.isEmpty()) {
                log.warn("用户ID或题目ID为空，无法查询错因信息");
                return null;
            }

            MistakeQuestion mistakeQuestion = mistakeQuestionDao.selectByUserIdAndQuestionId(userId, questionId);
            if (mistakeQuestion == null) {
                log.info("未找到对应的错题记录: userId={}, questionId={}", userId, questionId);
                return null;
            }

            // 转换为领域值对象
            int otherReasonFlag = (mistakeQuestion.getOtherReasonFlag() != null && mistakeQuestion.getOtherReasonFlag() == 1) ? 1 : 0;

            return MistakeReasonVO.builder()
                    .userId(mistakeQuestion.getUserId())
                    .questionId(mistakeQuestion.getQuestionId())
                    .isCareless(mistakeQuestion.getIsCareless())
                    .isUnfamiliar(mistakeQuestion.getIsUnfamiliar())
                    .isCalculateErr(mistakeQuestion.getIsCalculateErr())
                    .isTimeShortage(mistakeQuestion.getIsTimeShortage())
                    .otherReason(otherReasonFlag)
                    .otherReasonText(mistakeQuestion.getOtherReason())
                    .build();
        } catch (Exception e) {
            log.error("获取错因信息时发生异常: userId={}, questionId={}", userId, questionId, e);
            return null;
        }
    }

    @Override
    public boolean updateMistakeReasons(MistakeReasonVO reasonVO) {
        try {
            if (reasonVO == null) {
                log.warn("错因值对象为空，无法更新错因信息");
                return false;
            }

            // 构建数据库实体
            MistakeQuestion mistakeQuestion = new MistakeQuestion();
            mistakeQuestion.setUserId(reasonVO.getUserId());
            mistakeQuestion.setQuestionId(reasonVO.getQuestionId());
            mistakeQuestion.setIsCareless(reasonVO.getIsCareless() != null ? reasonVO.getIsCareless() : 0);
            mistakeQuestion.setIsUnfamiliar(reasonVO.getIsUnfamiliar() != null ? reasonVO.getIsUnfamiliar() : 0);
            mistakeQuestion.setIsCalculateErr(reasonVO.getIsCalculateErr() != null ? reasonVO.getIsCalculateErr() : 0);
            mistakeQuestion.setIsTimeShortage(reasonVO.getIsTimeShortage() != null ? reasonVO.getIsTimeShortage() : 0);

            // 处理otherReason字段：如果为1，保留现有文本；如果为0，设置为空字符串
            if (reasonVO.getOtherReason() != null && reasonVO.getOtherReason() == 1) {
                // 如果为1，保留现有文本或使用新文本
                mistakeQuestion.setOtherReason(reasonVO.getOtherReasonText() != null ?
                        reasonVO.getOtherReasonText() : "");
                mistakeQuestion.setOtherReasonFlag(1);
            } else {
                mistakeQuestion.setOtherReason("");
                mistakeQuestion.setOtherReasonFlag(0);
            }

            // 执行更新
            int result = mistakeQuestionDao.updateMistakeReasons(mistakeQuestion);
            boolean success = result > 0;

            if (success) {
                log.info("错因信息更新成功: userId={}, questionId={}",
                        reasonVO.getUserId(), reasonVO.getQuestionId());
            } else {
                log.error("错因信息更新失败: userId={}, questionId={}",
                        reasonVO.getUserId(), reasonVO.getQuestionId());
            }

            return success;
        } catch (Exception e) {
            log.error("更新错因信息时发生异常: userId={}, questionId={}",
                    reasonVO != null ? reasonVO.getUserId() : null,
                    reasonVO != null ? reasonVO.getQuestionId() : null, e);
            return false;
        }
    }

    @Override
    public boolean updateOtherReasonText(String userId, String questionId, String otherReasonText) {
        try {
            if (userId == null || userId.isEmpty() || questionId == null || questionId.isEmpty()) {
                log.warn("用户ID或题目ID为空，无法更新其他原因文本");
                return false;
            }

            // 执行更新
            int result = mistakeQuestionDao.updateOtherReasonText(userId, questionId, otherReasonText);
            boolean success = result > 0;

            if (success) {
                log.info("其他原因文本更新成功: userId={}, questionId={}", userId, questionId);
            } else {
                log.error("其他原因文本更新失败: userId={}, questionId={}", userId, questionId);
            }

            return success;
        } catch (Exception e) {
            log.error("更新其他原因文本时发生异常: userId={}, questionId={}", userId, questionId, e);
            return false;
        }
    }
}
