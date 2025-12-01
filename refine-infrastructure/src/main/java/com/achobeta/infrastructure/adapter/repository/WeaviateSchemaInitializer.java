package com.achobeta.infrastructure.adapter.repository;

import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.schema.model.DataType;
import io.weaviate.client.v1.schema.model.Property;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auth : Malog
 * @Desc : Weaviate Schema初始化器
 * @Time : 2025/12/01
 */
@Slf4j
@Component
public class WeaviateSchemaInitializer implements CommandLineRunner {

    @Autowired
    private WeaviateClient weaviateClient;

    @Value("${weaviate.class-name:LearningVector}")
    private String className;

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("开始初始化Weaviate Schema，className: {}", className);
            
            // 检查Schema是否已存在
            if (isSchemaExists()) {
                log.info("Weaviate Schema已存在，跳过初始化，className: {}", className);
                return;
            }
            
            // 创建Schema
            createSchema();
            log.info("Weaviate Schema初始化完成，className: {}", className);
            
        } catch (Exception e) {
            log.error("Weaviate Schema初始化失败", e);
            // 不抛出异常，避免影响应用启动
        }
    }

    /**
     * 检查Schema是否存在
     */
    private boolean isSchemaExists() {
        try {
            Result<WeaviateClass> result = weaviateClient.schema().classGetter()
                    .withClassName(className)
                    .run();
            
            return !result.hasErrors() && result.getResult() != null;
        } catch (Exception e) {
            log.warn("检查Schema是否存在时发生异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 创建Schema
     */
    private void createSchema() {
        try {
            // 定义属性
            List<Property> properties = new ArrayList<>();
            
            // 用户ID
            properties.add(Property.builder()
                    .name("userId")
                    .dataType(List.of(DataType.TEXT))
                    .description("用户ID")
                    .build());
            
            // 题目ID
            properties.add(Property.builder()
                    .name("questionId")
                    .dataType(List.of(DataType.TEXT))
                    .description("题目ID")
                    .build());
            
            // 题目内容
            properties.add(Property.builder()
                    .name("questionContent")
                    .dataType(List.of(DataType.TEXT))
                    .description("题目内容")
                    .build());
            
            // 行为类型
            properties.add(Property.builder()
                    .name("actionType")
                    .dataType(List.of(DataType.TEXT))
                    .description("行为类型")
                    .build());
            
            // 科目
            properties.add(Property.builder()
                    .name("subject")
                    .dataType(List.of(DataType.TEXT))
                    .description("科目")
                    .build());
            
            // 知识点ID
            properties.add(Property.builder()
                    .name("knowledgePointId")
                    .dataType(List.of(DataType.INT))
                    .description("知识点ID")
                    .build());
            
            // 创建时间
            properties.add(Property.builder()
                    .name("createdAt")
                    .dataType(List.of(DataType.TEXT))
                    .description("创建时间")
                    .build());
            
            // 更新时间
            properties.add(Property.builder()
                    .name("updatedAt")
                    .dataType(List.of(DataType.TEXT))
                    .description("更新时间")
                    .build());

            // 创建类定义
            WeaviateClass weaviateClass = WeaviateClass.builder()
                    .className(className)
                    .description("学习行为向量数据")
                    .properties(properties)
                    .vectorizer("text2vec-cohere") // 使用Cohere向量化器，适合云实例
                    .build();

            // 创建Schema
            Result<Boolean> result = weaviateClient.schema().classCreator()
                    .withClass(weaviateClass)
                    .run();

            if (result.hasErrors()) {
                log.error("创建Weaviate Schema失败: {}", result.getError().getMessages());
            } else {
                log.info("成功创建Weaviate Schema，className: {}", className);
            }

        } catch (Exception e) {
            log.error("创建Weaviate Schema时发生异常", e);
        }
    }
}