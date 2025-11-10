package com.achobeta.domain.mistake.service;

import com.achobeta.domain.mistake.model.valobj.MistakeReasonVO;

/**
 * @Auth : Malog
 * @Desc : 错因管理领域服务接口
 * @Time : 2025/11/10
 */
public interface IMistakeReasonService {

    /**
     * 切换错因状态
     *
     * @param reasonVO 错因值对象
     * @param reasonName 要切换的错因名称
     * @return 错因管理响应
     */
    MistakeReasonVO toggleMistakeReason(
            MistakeReasonVO reasonVO,
            String reasonName);

    /**
     * 更新其他原因文本
     *
     * @param reasonVO 错因值对象
     * @return 错因管理响应
     */
    MistakeReasonVO updateOtherReasonText(
            MistakeReasonVO reasonVO);

    /**
     * 获取错因信息
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @return 错因管理响应
     */
    MistakeReasonVO getMistakeReasons(
            String userId,
            String questionId);
}
