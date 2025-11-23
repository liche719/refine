package com.achobeta.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface KnowledgeMapper {

    /**
     * 根据知识点ID查询知识点名称
     * @param knowledgePointId 知识点ID
     * @return 知识点名称
     */
    @Select("SELECT knowledge_desc FROM knowledgePoint WHERE knowledge_point_id = #{knowledgePointId}")
    String findKnowledgeNameById(Integer knowledgePointId);

}
