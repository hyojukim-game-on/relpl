package com.ssafy.relpl.db.redis.entity;

public class MonthlyRanking {
    // 랭킹 데이터 필드 정의
    Long monthlyRankingId;
    Long userId;
    String monthlyEndTime;
    int monthlyDistance;
    // Redis 에 저장되는 랭킹 데이터
}
