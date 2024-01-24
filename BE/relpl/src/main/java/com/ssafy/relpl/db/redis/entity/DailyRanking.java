package com.ssafy.relpl.db.redis.entity;

public class DailyRanking {
    // 랭킹 데이터 필드 정의
    Long dailyRankingId;
    Long userId;
    String dailyEndTime;
    int dailyDistance;
    // Redis 에 저장되는 랭킹 데이터
}
