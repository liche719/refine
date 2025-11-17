package com.achobeta.domain.keypoints_explanation.service;

import com.achobeta.domain.keypoints_explanation.model.valobj.KeyPointsVO;
import com.achobeta.domain.keypoints_explanation.model.valobj.WrongQuestionVO;

import java.util.Collection;
import java.util.List;

public interface IKeyPointsExplanationService {
    List<KeyPointsVO> getSonKeyPoints(int knowledgeId, String userId);

    List<KeyPointsVO> getKeyPoints(String subject, String userId);

    String getKnowledgedescById(int knowledgeId, String userId);

    WrongQuestionVO getRelatedWrongQuecstions(int knowledgeId, String userId);

    List<KeyPointsVO> getRelatedKnowledgePoints(int knowledgeId, String userId);

    Boolean savedNote(String note, int knowledgeId, String userId);

}
