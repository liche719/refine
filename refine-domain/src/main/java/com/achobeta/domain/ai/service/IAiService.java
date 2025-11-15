package com.achobeta.domain.ai.service;

/**
 * @Auth : Malog
 * @Desc : 抽取第一个问题
 * @Time : 2025/11/2 14:34
 */
public interface IAiService {

    /**
     * 抽取第一个问题
     *
     * @param content 文件内容
     * @return 第一个问题
     */
    String extractTheFirstQuestion(String content);

    void aiSolveQuestion(String question, java.util.function.Consumer<String> contentCallback);

}
