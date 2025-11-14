package com.achobeta.domain.Feetback.adapter.repository;

import com.achobeta.domain.Feetback.model.entity.MistakeQuestionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MistakeQuestionMapper {

    List<MistakeQuestionEntity> selectByCondition(
            @Param("userId") String userId,
            @Param("keyword") String keyword,
            @Param("subjects") List<String> subjects,
            @Param("errorTypeFields") List<String> errorTypeFields,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}