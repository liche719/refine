package com.achobeta.domain.overview.adapter.repository;

import com.achobeta.domain.overview.model.valobj.StudyOverviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface IStudyOverviewRepository {

    @Select("select questions_num, review_rate, hard_questions, study_time" +
            " from userdata where user_id = #{userId}")
    StudyOverviewVO queryStudyOverview(String userId);

    @Select("select count(*) from mistakequestion where user_id = #{userId} and create_time > #{localDateTime}")
    Integer countByUserIdAndCreateTimeAfter(String userId, LocalDateTime localDateTime);

    @Select("select count(*) from mistakequestion where user_id = #{userId} and update_time between #{localDateTime} and #{localDateTime1}")
    Integer countByUserIdAndUpdateTimeBetween(String userId, LocalDateTime localDateTime, LocalDateTime localDateTime1);
}
