package com.achobeta.domain.keypoints_explanation.service.etendbiz;

import cn.hutool.core.date.DateTime;
import com.achobeta.domain.Feetback.adapter.repository.ErrorTypeMapper;
import com.achobeta.domain.keypoints_explanation.adapter.repository.KeyPointsMapper;
import com.achobeta.domain.keypoints_explanation.adapter.repository.SubjectTransportMappeer;
import com.achobeta.domain.keypoints_explanation.model.valobj.KeyPointsVO;
import com.achobeta.domain.keypoints_explanation.model.valobj.ToolTipVO;
import com.achobeta.domain.keypoints_explanation.model.valobj.WrongQuestionVO;
import com.achobeta.domain.keypoints_explanation.service.IKeyPointsExplanationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class KeyPointsExplanationService implements IKeyPointsExplanationService {
    @Autowired
    private KeyPointsMapper keyPointsMapper;

    /**
     * 获取子知识点
     * @param knowledgeId 父知识点id
     * @return
     */
    @Override
    public List<KeyPointsVO> getSonKeyPoints(int knowledgeId, String userId) {
        List<KeyPointsVO> keyPointsVOList = keyPointsMapper.getSonKeyPoints(knowledgeId, userId);
        return keyPointsVOList == null ? new ArrayList<>() : keyPointsVOList;
    }

    /**
     * 获取中心知识点
     * @param subject
     * @return
     */
    @Override
    public List<KeyPointsVO> getKeyPoints(String subject, String userId) {
        int subjectId = 0;
        subjectId = SubjectTransportMappeer.FIELD_MAP.get(subject);
        return keyPointsMapper.getKeyPoints(subjectId, userId);

    }

    /**
     * 获取知识点详情
     */
    @Override
    public String getKnowledgedescById(int knowledgeId, String userId) {
        return keyPointsMapper.getKnowledgedescById(knowledgeId, userId);
    }

    /**
     * 获取知识点错误题数
     * @param knowledgeId
     * @return
     */
    @Override
    public WrongQuestionVO getRelatedWrongQuestionsStatistic(int knowledgeId, String userId) {
        WrongQuestionVO wrongQuestionVO = keyPointsMapper.getRelatedWrongQuecstions(knowledgeId, userId);
        return wrongQuestionVO == null ? new WrongQuestionVO() : wrongQuestionVO;
    }

    /**
     * 获取知识点相关知识点
     * @param knowledgeId
     * @return
     */
    @Override
    public List<KeyPointsVO> getRelatedKnowledgePoints(int knowledgeId, String userId) {
        List<KeyPointsVO> keyPointsVOList = new ArrayList<>();

        // 获取父知识点
        KeyPointsVO parentKeyPoints = keyPointsMapper.getParentKeyPoints(knowledgeId, userId);
        if(parentKeyPoints != null){
            keyPointsVOList.add(parentKeyPoints);
            knowledgeId = parentKeyPoints.getId();

            // 获取同级知识点
            keyPointsVOList.addAll(keyPointsMapper.getSonKeyPoints(knowledgeId, userId));
            return keyPointsVOList;
        }
        return null;
    }

    /**
     * 保存或更新知识点笔记
     * @param note
     * @param knowledgeId
     * @return
     */
    @Override
    public void savedNote(String note, int knowledgeId, String userId) {
        keyPointsMapper.savedNote(note, knowledgeId, userId);
    }

    /**
     * 标记为已掌握
     * @param knowledgeId
     * @return
     */
    @Override
    public void markAsMastered(int knowledgeId, String userId) {
        keyPointsMapper.markAsMastered(knowledgeId, userId);
    }

    /**
     * 重命名知识点
     * @param knowledgeId
     * @param newName
     * @return
     */
    @Override
    public void renameNode(int knowledgeId, String newName, String userId) {
        keyPointsMapper.renameNode(knowledgeId, newName, userId);
    }

    /**
     * 获取知识点提示信息
     * @param knowledgeId
     * @return
     */
    @Override
    public ToolTipVO gettooltipById(int knowledgeId, String userId) {
        // 获取总错题数
        int total = keyPointsMapper.getTotalById(knowledgeId, userId);

        // 获取状态为0的错题数
        int count = keyPointsMapper.getCountById(knowledgeId, userId);

        // 获取最后一次复习时间
        String lastReviewTime = keyPointsMapper.getLastReviewTimeById(knowledgeId, userId);

        return new ToolTipVO(total, count, lastReviewTime);
    }
}
