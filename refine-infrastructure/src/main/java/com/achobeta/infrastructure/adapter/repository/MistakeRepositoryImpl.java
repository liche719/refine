package com.achobeta.infrastructure.adapter.repository;

import com.achobeta.domain.question.adapter.repository.IMistakeRepository;
import com.achobeta.domain.question.model.entity.MistakeQuestionEntity;
import com.achobeta.domain.question.model.po.MistakeKnowledgePO;
import com.achobeta.infrastructure.dao.MistakeQuestionMapper;
import com.achobeta.infrastructure.dao.po.MistakePO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class MistakeRepositoryImpl implements IMistakeRepository {

    private final MistakeQuestionMapper mistakeQuestionMapper;

    @Override
    public void save(MistakeQuestionEntity mistakeEntity) {
        MistakePO mistakePO = new MistakePO();
        BeanUtils.copyProperties(mistakeEntity, mistakePO);
        mistakePO.setUpdateTime(LocalDateTime.now()); // 补充领域层未定义的字段
        mistakeQuestionMapper.insert(mistakePO);
    }

    @Override
    public MistakeKnowledgePO findSubjectAndKnowledgeIdById(Integer mistakeQuestionId) {
        return mistakeQuestionMapper.findSubjectAndKnowledgeIdById(mistakeQuestionId);
    }


}