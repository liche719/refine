package com.achobeta.domain.keypoints_explanation.model.valobj;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SonPointVO {
    private String pointId; // 知识点id
    private String pointName; // 知识点名称
    private String pointDesc; // 知识点描述
    private List<SonPointVO> sonPoints = new ArrayList<>();
}
