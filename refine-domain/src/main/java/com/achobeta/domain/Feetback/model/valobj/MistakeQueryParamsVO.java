package com.achobeta.domain.Feetback.model.valobj;

import com.achobeta.types.enums.TimeRange;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MistakeQueryParamsVO {
    private String userId;
    private String keyword;
    private List<String> subject;
    private List<String> errorType;
    private TimeRange timeRange;
    private int page;
    private int size;
}
