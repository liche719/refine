package com.achobeta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    /**
     * 定义异步线程池
     */
    @Bean(name = "mistakeExecutor")
    public Executor mistakeExecutor() {
        // 根据实际需求调整核心线程数、队列大小等参数
        return Executors.newFixedThreadPool(5, r -> {
            Thread thread = new Thread(r);
            thread.setName("mistake-record-thread-"); // 线程名前缀，便于日志追踪
            thread.setDaemon(true); // 守护线程，避免应用退出时阻塞
            return thread;
        });
    }
}