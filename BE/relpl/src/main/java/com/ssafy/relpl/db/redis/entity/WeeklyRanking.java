package com.ssafy.relpl.db.redis.entity;

public class WeeklyRanking {
    // 랭킹 데이터 필드 정의
    Long weeklyRankingId;
    Long userId;
    String weeklyEndTime;
    int weeklyDistance;
    // Redis 에 저장되는 랭킹 데이터
}
