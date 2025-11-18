package com.achobeta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AiThreadPoolConfig {

    // 最大并发数
    @Value("${ai.dashscope.max-concurrency}")
    private int aiMaxConcurrency;

    @Bean
    public ThreadPoolTaskExecutor aiExclusiveThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int cpuCore = Runtime.getRuntime().availableProcessors();

        int baseCoreSize = cpuCore * 2 + 1;

        int corePoolSize = Math.max(baseCoreSize, aiMaxConcurrency);

        executor.setCorePoolSize(corePoolSize);

        // 最大线程数：从配置文件读取，确保不超过系统承受能力
        // 若配置值小于核心线程数，则强制使用核心线程数作为上限
        executor.setMaxPoolSize(corePoolSize * 2);

        // 设置缓冲队列大小，避免任务瞬间涌入导致OOM
        executor.setQueueCapacity(corePoolSize * 5);

        // 超过核心线程数的线程，空闲120秒后销毁，
        executor.setKeepAliveSeconds(120);

        // 线程名称前缀，便于日志排查
        executor.setThreadNamePrefix("ai-exclusive-thread-");

        // 当线程池和队列都满时，让提交任务的线程自己执行，避免任务丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();//初始化
        return executor;
    }

}
