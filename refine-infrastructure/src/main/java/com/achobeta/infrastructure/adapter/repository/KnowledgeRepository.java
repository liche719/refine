package com.achobeta.infrastructure.adapter.repository;

import com.achobeta.domain.question.adapter.repository.IKnowledgeRepository;
import com.achobeta.infrastructure.dao.KnowledgeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KnowledgeRepository implements IKnowledgeRepository {

    private final KnowledgeMapper knowledgeMapper;

    @Override
    public String findKnowledgeNameById(Integer knowledgePointId) {
        return knowledgeMapper.findKnowledgeNameById(knowledgePointId);
    }

    @Override
    public String findSubjectById(Integer knowledgePointId) {
        return knowledgeMapper.findSubjectById(knowledgePointId);
    }

}
