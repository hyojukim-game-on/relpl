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
public class RefreshToken {
    private String refreshToken;
    private Long userId;

    public static RefreshToken createRefreshToken(String refreshToken, Long userId) {
        return RefreshToken.builder()
                .refreshToken(refreshToken)
                .userId(userId)
                .build();
    }

}