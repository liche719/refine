package com.achobeta.trigger.event;

import com.achobeta.domain.rag.service.impl.LearningAnalysisService;
import com.achobeta.domain.rag.model.valobj.LearningDynamicVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auth : Malog
 * @Desc : 用户登录事件监听器
 * @Time : 2025/11/25
 */
@Slf4j
@Component
public class UserLoginEventListener {
    
    @Autowired
    private LearningAnalysisService learningAnalysisService;
    
    /**
     * 监听用户登录事件
     */
    @EventListener
    public void handleUserLoginEvent(UserLoginEvent event) {
        try {
            String userId = event.getUserId();
            log.info("检测到用户登录，开始分析学习动态，userId:{}", userId);
            
            // 异步分析用户学习动态
            List<LearningDynamicVO> dynamics = learningAnalysisService.onUserLogin(userId);
            
            // 这里可以将分析结果缓存到Redis，供前端获取
            // 或者通过WebSocket推送给前端
            
        } catch (Exception e) {
            log.error("处理用户登录事件失败", e);
        }
    }
}