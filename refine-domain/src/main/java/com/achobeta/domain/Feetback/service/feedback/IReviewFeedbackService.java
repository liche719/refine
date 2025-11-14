package com.achobeta.domain.Feetback.service.feedback;

import com.achobeta.domain.Feetback.model.entity.MistakeQuestionEntity;
import com.achobeta.domain.Feetback.model.valobj.MistakeQueryParamsVO;
import com.achobeta.domain.Feetback.model.valobj.OverdueCountVO;
import com.achobeta.domain.Feetback.model.valobj.StatsVO;
import com.achobeta.domain.Feetback.model.valobj.TrickyKnowledgePointVO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IReviewFeedbackService {

    /**
     * 获取用户待复习题目数量
     * @param userId
     * @return
     */
    OverdueCountVO getOverdueCount(String userId);

    /**
     * 获取用户待复习题目列表
     * @param params
     * @return
     */
    Page<MistakeQuestionEntity> searchAndFilter(MistakeQueryParamsVO params);

    /**
     * 批量删除用户待复习题目
     * @param userId
     * @param questionIds
     */
    void deleteBatch(String userId, List<Integer> questionIds);

    /**
     * 获取用户待复习题目统计信息
     * @param userId
     * @return
     */
    StatsVO getStatistics(String userId);

    /**
     * 获取用户近期出错最多的知识点
     * @param userId
     * @return
     */
    List<TrickyKnowledgePointVO> getTrickyKnowledgePoint(String userId);
}
