package com.achobeta.domain.keypoints_explanation.service;

import com.achobeta.domain.keypoints_explanation.model.valobj.*;

import java.util.Collection;
import java.util.List;

public interface IKeyPointsExplanationService {
    List<KeyPointsVO> getSonKeyPoints(int knowledgeId, String userId);

    List<KeyPointsVO> getKeyPoints(String subject, String userId);

    String getKnowledgedescById(int knowledgeId, String userId);

    WrongQuestionVO getRelatedWrongQuestionsStatistic(int knowledgeId, String userId);

    List<KeyPointsVO> getRelatedKnowledgePoints(int knowledgeId, String userId);

    void savedNote(String note, int knowledgeId, String userId);

    void markAsMastered(int knowledgeId, String userId);

    void renameNode(int knowledgeId, String newName, String userId);

    ToolTipVO gettooltipById(int knowledgeId, String userId);

    RelateQuestionVO getRelatedWrongQuestions(int knowledgeId, String userId);

    void addSonPoint(SonPointVO sonPointVOs, String userId, String parentId);
}
