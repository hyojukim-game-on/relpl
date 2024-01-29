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
    private String dailyEndTime; // 만료시간 관련 어떻게 설정할 건지 로직을 생각해야 함
    private int dailyDistance;
}
