package com.achobeta.domain.aisuggession.adapter.repository;

import com.achobeta.domain.aisuggession.model.entity.KnowledgePointEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AILearningSuggessionRepository {
    /**
     * 获取用户近半个月学习数据
     * @param userId
     * @return
     */

    List<KnowledgePointEntity> getKeyPoint(String userId);
}
