package com.ssafy.relpl.db.redis.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("MonthlyRanking")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyRanking {
    // 랭킹 데이터 필드 정의
    Long monthlyRankingId;
    Long userId;
    String monthlyEndTime;
    int monthlyDistance;
    // Redis 에 저장되는 랭킹 데이터
    // 24.01.29 12:30PM codeReview
}
