package com.achobeta.domain.keypoints_explanation.model.aggregate;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 删除列表
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteListAggregate {
    private String userId;// 用户id
    private String knowledgeId;// 知识点id
    private LocalDateTime deletedAt;// 删除时间
}
