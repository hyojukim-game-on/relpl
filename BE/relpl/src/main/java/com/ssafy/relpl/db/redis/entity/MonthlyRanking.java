//package com.ssafy.relpl.db.redis.entity;
//
//
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.Id;
//import lombok.*;
//import org.springframework.data.redis.core.RedisHash;
//import org.springframework.data.redis.core.TimeToLive;
//import org.springframework.data.redis.core.index.Indexed;
//
//import java.util.concurrent.TimeUnit;
//
//@RedisHash("monthlyranking") // JPA 의 @Entity 와 동일한 역할
//@Getter
//@AllArgsConstructor
//@Builder
//public class MonthlyRanking {
//    @Id
//    @GeneratedValue
//    private Long Id;
//
//    @Indexed
//    private String nickname;
//
//    @Indexed
//    @TimeToLive(unit = TimeUnit.DAYS)
//    private String rankingTime;
//
//    private int moveTotalDistance;
//}
