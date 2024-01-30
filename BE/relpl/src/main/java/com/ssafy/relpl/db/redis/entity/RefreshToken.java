package com.ssafy.relpl.db.redis.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "refresh", timeToLive = 60*60)
public class RefreshToken {

    @Id
    private Long id;

    private String refreshToken;

    @Indexed
    private String accessToken;
}