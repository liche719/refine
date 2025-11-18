package com.achobeta.domain.keypoints_explanation.adapter.repository;

import cn.hutool.core.date.DateTime;
import com.achobeta.domain.keypoints_explanation.model.valobj.KeyPointsVO;
import com.achobeta.domain.keypoints_explanation.model.valobj.ToolTipVO;
import com.achobeta.domain.keypoints_explanation.model.valobj.WrongQuestionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KeyPointsMapper {
    @Select("select knowledge_point_id as id, knowledge_point_name as keyPoints from knowledgepoint" +
            " where parent_knowledge_point_id = #{knowledgeId} and user_id = #{userId}")
    List<KeyPointsVO> getSonKeyPoints(@Param("knowledgeId") int knowledgeId, @Param("userId") String userId);

    @Select("select knowledge_point_id as id, knowledge_point_name as keyPoints from knowledgepoint" +
            " where parent_knowledge_point_id = #{subjectId} and user_id = #{userId}")
    List<KeyPointsVO> getKeyPoints(int subjectId, String userId);

    @Select("select knowledge_desc from knowledgepoint" +
            " where knowledge_point_id = #{knowledgeId} and user_id = #{userId}")
    String getKnowledgedescById(int knowledgeId, String userId);

    @Select("select count(id) as updateCount, count(update_time >= SUBDATE(now(), 7)) as reviewCount from mistakequestion" +
            " where knowledge_point_id = #{knowledgeId} and user_id = #{userId}")
    WrongQuestionVO getRelatedWrongQuecstions(int knowledgeId, String userId);

    @Select("select k.parent_knowledge_point_id as id, k1.knowledge_point_name as keyPoints from knowledgepoint k " +
            "join knowledgepoint k1 on k.parent_knowledge_point_id = k1.knowledge_point_id" +
            " where k.knowledge_point_id = #{knowledgeId} and k.user_id = #{userId}")
    KeyPointsVO getParentKeyPoints(int knowledgeId, String userId);
    
    @Select("update knowledgepoint set note = #{note} where knowledge_point_id = #{knowledgeId} and user_id = #{userId}")
    void savedNote(String note, int knowledgeId, String userId);

    @Select("update knowledgepoint set status = 1  where knowledge_point_id = #{knowledgeId} and user_id = #{userId}")
    void markAsMastered(int knowledgeId, String userId);

    @Select("update knowledgepoint set knowledge_point_name = #{newName} where knowledge_point_id = #{knowledgeId} and user_id = #{userId}")
    void renameNode(int knowledgeId, String newName, String userId);

    @Select("select count(id) from mistakequestion where knowledge_point_id = #{knowledgeId} and user_id = #{userId}")
    int getTotalById(int knowledgeId, String userId);

    @Select("select count(id) from mistakequestion where knowledge_point_id = #{knowledgeId} and user_id = #{userId} and question_status = 0")
    int getCountById(int knowledgeId, String userId);

    @Select("select max(update_time) from mistakequestion where knowledge_point_id = #{knowledgeId} and user_id = #{userId}")
    String getLastReviewTimeById(int knowledgeId, String userId);
}
