package com.achobeta.infrastructure.dao;

import com.achobeta.infrastructure.dao.po.MistakeQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Auth : Malog
 * @Desc : 错题数据访问接口
 * @Time : 2025/11/10
 */
@Mapper
public interface IMistakeQuestionDao {

    /**
     * 插入错题记录
     *
     * @param mistakeQuestion 错题实体
     * @return 影响行数
     */
    int insert(MistakeQuestion mistakeQuestion);

    /**
     * 根据题目ID查询错题
     *
     * @param questionId 题目ID
     * @return 错题实体
     */
    MistakeQuestion selectByQuestionId(String questionId);

    /**
     * 根据用户ID查询错题列表
     *
     * @param userId 用户ID
     * @return 错题列表
     */
    java.util.List<MistakeQuestion> selectByUserId(String userId);

    /**
     * 根据用户ID和题目ID查询错题
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 错题实体
     */
    MistakeQuestion selectByUserIdAndQuestionId(String userId, String questionId);

    /**
     * 更新错因信息
     *
     * @param mistakeQuestion 错题实体
     * @return 影响行数
     */
    int updateMistakeReasons(MistakeQuestion mistakeQuestion);

    /**
     * 更新其他原因文本
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @param otherReasonText 其他原因文本
     * @return 影响行数
     */
    int updateOtherReasonText(String userId, String questionId, String otherReasonText);
}
