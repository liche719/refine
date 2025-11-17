package com.achobeta.domain.keypoints_explanation.service.etendbiz;

import com.achobeta.domain.keypoints_explanation.adapter.repository.KeyPointsMapper;
import com.achobeta.domain.keypoints_explanation.model.valobj.KeyPointsVO;
import com.achobeta.domain.keypoints_explanation.model.valobj.WrongQuestionVO;
import com.achobeta.domain.keypoints_explanation.service.IKeyPointsExplanationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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
        return keyPointsMapper.getKeyPoints(subject, userId);
    }

    /**
     * 获取知识点详情
     */
    @Override
    public String getKnowledgedescById(int knowledgeId, String userId) {
        return keyPointsMapper.getKnowledgedescById(knowledgeId, userId);
    }

    @Override
    public WrongQuestionVO getRelatedWrongQuecstions(int knowledgeId, String userId) {
        WrongQuestionVO wrongQuestionVO = keyPointsMapper.getRelatedWrongQuecstions(knowledgeId, userId);
        return wrongQuestionVO == null ? new WrongQuestionVO() : wrongQuestionVO;
    }

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

    @Override
    public Boolean savedNote(String note, int knowledgeId, String userId) {

        return keyPointsMapper.savedNote(note, knowledgeId, userId);
    }
}
