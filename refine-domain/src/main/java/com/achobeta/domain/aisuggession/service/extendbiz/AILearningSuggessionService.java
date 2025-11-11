package com.achobeta.domain.aisuggession.service.extendbiz;

import com.achobeta.domain.aisuggession.adapter.repository.AILearningSuggessionRepository;
import com.achobeta.domain.aisuggession.model.entity.KnowledgePointEntity;
import com.achobeta.domain.aisuggession.model.valobj.KeyPointVO;
import com.achobeta.domain.aisuggession.service.ConsultantService;
import com.achobeta.domain.aisuggession.service.IAILearningSuggessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AILearningSuggessionService implements IAILearningSuggessionService {

    @Autowired
    private AILearningSuggessionRepository repository;

    @Autowired
    private ConsultantService consultantService;

    @Override
    public List<KeyPointVO> getKeyPoint(int userId) {
        List<KnowledgePointEntity> entities = repository.getKeyPoint(userId);
        try {
            if (entities != null && !entities.isEmpty()) {
                //entities.forEach(KnowledgePointEntity::toString);
                String aiResponse  = consultantService.chat("这是用户最近的学习数据，" + entities +
                        "请根据这些数据，给出1-2条具体、可操作的学习建议。每条建议格式为：“【知识点】建议内容”。" +
                        "不要解释，直接输出建议。");
                return parseAdvice(aiResponse);
            }else{
                List<KeyPointVO> fallback = new ArrayList<>();
                fallback.add(new KeyPointVO("无学习数据", "请先完成学习，再查看建议"));
                return fallback;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 回退到默认建议
            List<KeyPointVO> fallback = new ArrayList<>();
            fallback.add(new KeyPointVO("系统异常", "AI 分析暂时不可用，请稍后再试"));
            return fallback;
        }
    }

    // 简单解析 AI 返回的带【】格式
    private List<KeyPointVO> parseAdvice(String text) {
        List<KeyPointVO> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("【(.*?)】(.*)");
        String[] lines = text.split("\n");

        for (String line : lines) {
            Matcher m = pattern.matcher(line.trim());
            if (m.find()) {
                String topic = m.group(1).trim();
                String suggestion = m.group(2).trim();
                list.add(new KeyPointVO(topic, suggestion));
            }
        }

        // 如果没匹配到，整段作为一条建议
        if (list.isEmpty() && !text.trim().isEmpty()) {
            list.add(new KeyPointVO("综合建议", text.trim()));
        }

        return list;
    }
}
