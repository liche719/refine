package com.achobeta.infrastructure.gateway;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @Auth : Malog
 * @Desc : 阿里云DashScope嵌入服务
 * @Time : 2025/11/25
 */
@Slf4j
@Service
public class DashScopeEmbeddingService {
    
    @Value("${dashscope.apiKey}")
    private String apiKey;
    
    private static final String EMBEDDING_MODEL = "text-embedding-v1";
    private static final int VECTOR_DIMENSION = 384; // 与pgvector表结构保持一致
    
    /**
     * 生成文本嵌入向量
     */
    public float[] generateEmbedding(String text) {
        try {
            if (text == null || text.trim().isEmpty()) {
                log.warn("输入文本为空，返回零向量");
                return new float[VECTOR_DIMENSION];
            }
            
            TextEmbedding textEmbedding = new TextEmbedding();
            
            TextEmbeddingParam param = TextEmbeddingParam.builder()
                    .apiKey(apiKey)
                    .model(EMBEDDING_MODEL)
                    .texts(Arrays.asList(text.trim()))
                    .build();
            
            TextEmbeddingResult result = textEmbedding.call(param);
            
            if (result != null && result.getOutput() != null && !result.getOutput().getEmbeddings().isEmpty()) {
                List<Double> embedding = result.getOutput().getEmbeddings().get(0).getEmbedding();
                
                // 转换为float数组
                float[] vector = new float[embedding.size()];
                for (int i = 0; i < embedding.size(); i++) {
                    vector[i] = embedding.get(i).floatValue();
                }
                
                log.debug("成功生成嵌入向量，文本长度:{} 向量维度:{}", text.length(), vector.length);
                return vector;
            } else {
                log.error("嵌入向量生成失败，返回结果为空");
                return generateFallbackEmbedding(text);
            }
            
        } catch (ApiException e) {
            log.error("调用DashScope嵌入API失败，API错误: {}", e.getMessage());
            return generateFallbackEmbedding(text);
        } catch (NoApiKeyException e) {
            log.error("DashScope API Key未配置或无效: {}", e.getMessage());
            return generateFallbackEmbedding(text);
        } catch (Exception e) {
            log.error("生成嵌入向量时发生未知错误", e);
            return generateFallbackEmbedding(text);
        }
    }
    
    /**
     * 批量生成嵌入向量
     */
    public List<float[]> generateEmbeddings(List<String> texts) {
        try {
            if (texts == null || texts.isEmpty()) {
                log.warn("输入文本列表为空");
                return Arrays.asList();
            }
            
            // 过滤空文本
            List<String> validTexts = texts.stream()
                    .filter(text -> text != null && !text.trim().isEmpty())
                    .map(String::trim)
                    .collect(java.util.stream.Collectors.toList());
            
            if (validTexts.isEmpty()) {
                log.warn("没有有效的输入文本");
                return Arrays.asList();
            }
            
            TextEmbedding textEmbedding = new TextEmbedding();
            
            TextEmbeddingParam param = TextEmbeddingParam.builder()
                    .apiKey(apiKey)
                    .model(EMBEDDING_MODEL)
                    .texts(validTexts)
                    .build();
            
            TextEmbeddingResult result = textEmbedding.call(param);
            if (result != null && result.getOutput() != null && !result.getOutput().getEmbeddings().isEmpty()) {
                return result.getOutput().getEmbeddings().stream()
                        .map(embedding -> {
                            List<Double> embeddingList = embedding.getEmbedding();
                            float[] vector = new float[embeddingList.size()];
                            for (int i = 0; i < embeddingList.size(); i++) {
                                vector[i] = embeddingList.get(i).floatValue();
                            }
                            return vector;
                        })
                        .collect(java.util.stream.Collectors.toList());
            } else {
                log.error("批量嵌入向量生成失败，返回结果为空");
                return validTexts.stream()
                        .map(this::generateFallbackEmbedding)
                        .collect(java.util.stream.Collectors.toList());
            }
            
        } catch (Exception e) {
            log.error("批量生成嵌入向量失败", e);
            return texts.stream()
                    .map(this::generateFallbackEmbedding)
                    .collect(java.util.stream.Collectors.toList());
        }
    }
    
    /**
     * 备用的嵌入向量生成方法
     * 当DashScope API调用失败时使用
     */
    private float[] generateFallbackEmbedding(String text) {
        log.warn("使用备用方法生成嵌入向量，文本: {}", text != null ? text.substring(0, Math.min(text.length(), 50)) + "..." : "null");
        
        float[] vector = new float[VECTOR_DIMENSION];
        
        if (text == null || text.trim().isEmpty()) {
            return vector; // 返回零向量
        }
        
        // 基于文本内容生成确定性向量
        // 使用多个哈希函数来增加向量的质量
        String normalizedText = text.trim().toLowerCase();
        
        // 使用文本的多个特征来生成向量
        int hash1 = normalizedText.hashCode();
        int hash2 = normalizedText.length();
        int hash3 = normalizedText.chars().sum();
        
        // 组合多个种子
        long seed = ((long) hash1 << 32) | (hash2 ^ hash3);
        Random random = new Random(seed);
        
        // 生成向量
        for (int i = 0; i < VECTOR_DIMENSION; i++) {
            vector[i] = (float) (random.nextGaussian() * 0.1f); // 使用高斯分布，标准差0.1
        }
        
        // 归一化向量
        normalizeVector(vector);
        
        return vector;
    }
    
    /**
     * 向量归一化
     */
    private void normalizeVector(float[] vector) {
        float norm = 0;
        for (float v : vector) {
            norm += v * v;
        }
        norm = (float) Math.sqrt(norm);
        
        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }
    }
    
    /**
     * 计算两个向量的余弦相似度
     */
    public double calculateCosineSimilarity(float[] vector1, float[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += vector1[i] * vector1[i];
            norm2 += vector2[i] * vector2[i];
        }
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}