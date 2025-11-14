package com.achobeta.trigger.http;

import com.achobeta.api.dto.KeyPointDTO;
import com.achobeta.api.dto.TrickyKnowledgePointDTO;
import com.achobeta.domain.IRedisService;
import com.achobeta.domain.aisuggession.model.valobj.KeyPointVO;
import com.achobeta.domain.aisuggession.service.IAILearningSuggessionService;
import com.achobeta.types.common.Constants;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI学习建议接口
 */
@Slf4j
@Validated
@CrossOrigin("${app.config.cross-origin}:*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/${app.config.api-version}/ai_suggession")
public class AILearningSuggessionController {
    private final IAILearningSuggessionService service;
    private final IRedisService redis;

    @RequestMapping("/get_key_point")
    public List<KeyPointDTO> getKeyPoint(@RequestHeader("token") String token) {
        String userId = redis.getValue(Constants.USER_ID_KEY_PREFIX + token);
        log.info("用户获取AI学习建议，userId:{}", userId);
        List<KeyPointVO> keyPointVOS = service.getKeyPoint(userId);
        List<KeyPointDTO> keyPointDTOS = keyPointVOS.stream()
                .map(keyPointVO -> KeyPointDTO.builder()
                        .knowledgePoint(keyPointVO.getKnowledgePoint())
                        .reviewReason(keyPointVO.getReviewReason())
                        .build()).toList();
        return keyPointDTOS;
    }
}
