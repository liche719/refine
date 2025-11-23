package com.achobeta.domain.Feetback.adapter.repository;

import java.util.HashMap;
import java.util.Map;

public class ErrorTypeMapper {
    public static final Map<String, String> FIELD_MAP = new HashMap<>();

    static {
        FIELD_MAP.put("粗心马虎", "is_careless");
        FIELD_MAP.put("知识点不熟悉", "is_unfamiliar");
        FIELD_MAP.put("计算错误", "is_calculate_err");
        FIELD_MAP.put("时间不够", "is_time_shortage");
    }
}
