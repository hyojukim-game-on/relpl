package com.ssafy.relpl.db.redis.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;




@RedisHash("DailyRanking")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyRanking {
    // 랭킹 데이터 필드 정의
    @Id
    private Long dailyRankingId;
    private Long userId;
    // private String dailyEndTime;
    private int dailyDistance;
}
