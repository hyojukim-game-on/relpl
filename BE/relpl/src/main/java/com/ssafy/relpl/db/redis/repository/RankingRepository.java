package com.ssafy.relpl.db.redis.repository;


import com.ssafy.relpl.db.redis.entity.DailyRanking;
import com.ssafy.relpl.dto.response.RankingEntry;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;



@Repository
public interface RankingRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RankingRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addScore(String user, double score) {
        redisTemplate.opsForZSet().add("ranking", user, score);
    }

    public Set<String> getTopUsers(int top) {
        return redisTemplate.opsForZSet().reverseRange("ranking", 0, top - 1)
    }

}