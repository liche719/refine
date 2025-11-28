package com.achobeta.trigger.http;

import com.achobeta.domain.ai.service.IAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @Auth : Malog
 * @Desc : AI解题控制器 - 专门处理AI解题相关的请求
 * @Time : 2025/11/7 11:15
 */

@Slf4j
@Validated
@RestController()
@CrossOrigin("${app.config.cross-origin}:*")
@RequestMapping("/api/${app.config.api-version}/solve/")
@RequiredArgsConstructor
public class AiSolveController {

    private final IAiService aiService;

    @PostMapping("stream")
    public SseEmitter stream(@RequestParam("question") String question) {
        // 设置超时时间为5分钟
        SseEmitter emitter = new SseEmitter(300000L);
        
        // 添加连接状态标志
        final boolean[] isConnectionActive = {true};

        // 设置超时和完成回调
        emitter.onTimeout(() -> {
            log.warn("SSE connection timeout for question: {}", question);
            isConnectionActive[0] = false;
            emitter.complete();
        });

        emitter.onCompletion(() -> {
            log.info("SSE connection completed for question: {}", question);
            isConnectionActive[0] = false;
        });

        emitter.onError((ex) -> {
            log.error("SSE connection error for question: {}", question, ex);
            isConnectionActive[0] = false;
        });

        try {
            aiService.aiSolveQuestion(question, content -> {
                // 检查连接是否仍然活跃
                if (!isConnectionActive[0]) {
                    log.warn("Connection is no longer active, stopping content sending");
                    return;
                }
                
                try {
                    emitter.send(SseEmitter.event().data(content));
                } catch (IOException e) {
                    log.error("Error sending SSE event, marking connection as inactive", e);
                    isConnectionActive[0] = false;
                    // 不要在这里调用completeWithError，因为连接可能已经断开
                    // emitter.completeWithError(e);
                } catch (IllegalStateException e) {
                    // 处理连接已关闭的情况
                    log.warn("SSE connection already closed: {}", e.getMessage());
                    isConnectionActive[0] = false;
                }
            });

            // 只有在连接仍然活跃时才完成emitter
            if (isConnectionActive[0]) {
                emitter.complete();
            }

        } catch (Exception e) {
            log.error("Error during AI stream call", e);
            if (isConnectionActive[0]) {
                emitter.completeWithError(e);
            }
        }

        return emitter;
    }


}
