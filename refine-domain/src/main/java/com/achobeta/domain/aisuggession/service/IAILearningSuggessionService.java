package com.achobeta.domain.aisuggession.service;

import com.achobeta.domain.aisuggession.model.valobj.KeyPointVO;

import java.util.List;

public interface IAILearningSuggessionService {
    List<KeyPointVO> getKeyPoint(String userId);
}
