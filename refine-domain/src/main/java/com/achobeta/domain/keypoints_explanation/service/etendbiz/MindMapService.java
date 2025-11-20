package com.achobeta.domain.keypoints_explanation.service.etendbiz;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MindMapService {
    public String generateUUID() {
        return UUID.randomUUID().toString();  // 生成 36 位 UUID
    }
}
