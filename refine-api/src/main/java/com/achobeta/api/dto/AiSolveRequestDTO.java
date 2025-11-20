package com.achobeta.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auth : Malog
 * @Desc :
 * @Time : 2025/11/7 11:17
 */
@Data
@Builder
public class AiSolveRequestDTO implements Serializable {

    /**
     * 问题内容
     */
    private String questionContext;

}
