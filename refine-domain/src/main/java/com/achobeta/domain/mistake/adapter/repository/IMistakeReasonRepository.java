package com.achobeta.domain.mistake.adapter.repository;

import com.achobeta.domain.mistake.model.valobj.MistakeReasonVO;

/**
 * @Auth : Malog
 * @Desc : 错因管理仓储接口
 * @Time : 2025/11/10
 */
public interface IMistakeReasonRepository {

    /**
     * 获取错因信息
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 错因信息
     */
    MistakeReasonVO getMistakeReasons(String userId, String questionId);

    /**
     * 更新错因信息
     *
     * @param reasonVO 错因值对象
     * @return 是否更新成功
     */
    boolean updateMistakeReasons(MistakeReasonVO reasonVO);

    /**
     * 更新其他原因文本
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @param otherReasonText 其他原因文本
     * @return 是否更新成功
     */
    boolean updateOtherReasonText(String userId, String questionId, String otherReasonText);
}
