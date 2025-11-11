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
        SseEmitter emitter = new SseEmitter();

        try {
            aiService.aiSolveQuestion(question, content -> {
                try {
                    emitter.send(SseEmitter.event().data(content));
                } catch (IOException e) {
                    log.error("Error sending SSE event", e);
                    emitter.completeWithError(e);
                }
            });

            // 当aiSolveQuestion方法执行完毕（流式输出完成）后，完成emitter
            emitter.complete();

        } catch (Exception e) {
            log.error("Error during AI stream call", e);
            emitter.completeWithError(e);
        }

        return emitter;
    }


}
