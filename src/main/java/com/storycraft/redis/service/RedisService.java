package com.storycraft.redis.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 인증코드 저장 (10분 TTL)
    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, Duration.ofMinutes(10));
    }

    // 인증코드 조회
    public String getCode(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    // 인증코드 삭제
    public void deleteCode(String email) {
        redisTemplate.delete(email);
    }
}
