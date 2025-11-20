package com.achobeta.domain.question.adapter.repository;

public interface IKnowledgeRepository {

    String findKnowledgeNameById(Integer knowledgePointId);

    String findSubjectById(Integer knowledgePointId);
}
