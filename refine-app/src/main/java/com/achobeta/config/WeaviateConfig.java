package com.achobeta.config;

import io.weaviate.client.Config;
import io.weaviate.client.WeaviateAuthClient;
import io.weaviate.client.WeaviateClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auth : Malog
 * @Desc : Weaviate客户端配置
 * @Time : 2025/11/28
 */
@Slf4j
@Configuration
public class WeaviateConfig {

    @Value("${weaviate.config.host:localhost}")
    private String host;

    @Value("${weaviate.config.port:8080}")
    private int port;

    @Value("${weaviate.config.scheme:http}")
    private String scheme;

    @Value("${weaviate.config.api-key:}")
    private String apiKey;

    @Value("${weaviate.config.timeout:30000}")
    private int timeout;

    @Value("${weaviate.config.class-name:LearningVector}")
    private String className;

    /**
     * 创建Weaviate客户端Bean
     */
    @Bean
    public WeaviateClient weaviateClient() {
        try {
            // 构建连接URL - 对于云服务，不需要端口号
            String url;
            Config config;
            
            if (port == 443 && "https".equals(scheme)) {
                // Weaviate Cloud服务，不需要端口号
                url = scheme + "://" + host;
                config = new Config(scheme, host);
            } else {
                // 本地服务，需要端口号
                url = scheme + "://" + host + ":" + port;
                config = new Config(scheme, host + ":" + port);
            }

            WeaviateClient client;

            // 如果配置了API Key，使用认证客户端
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                log.info("使用API Key认证连接Weaviate");
                client = WeaviateAuthClient.apiKey(config, apiKey);
            } else {
                // 无认证连接
                log.info("使用无认证方式连接Weaviate");
                client = new WeaviateClient(config);
            }

            log.info("Weaviate客户端初始化成功，连接地址: {}", url);
            return client;

        } catch (Exception e) {
            log.error("Weaviate客户端初始化失败", e);
            throw new RuntimeException("Failed to initialize Weaviate client", e);
        }
    }

    /**
     * 获取Weaviate类名配置
     */
    @Bean
    public String weaviateClassName() {
        return className;
    }
}