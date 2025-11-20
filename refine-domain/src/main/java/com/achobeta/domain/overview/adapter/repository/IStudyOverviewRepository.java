package com.achobeta.domain.overview.adapter.repository;

import com.achobeta.domain.overview.model.valobj.StudyOverviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface IStudyOverviewRepository {

    @Select("select questions_num, review_rate, hard_questions, study_time" +
            " from userdata where user_id = #{userId}")
    StudyOverviewVO queryStudyOverview(String userId);
}
