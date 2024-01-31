package com.ssafy.relpl.service;

import com.ssafy.relpl.db.redis.entity.RefreshToken;
import com.ssafy.relpl.db.redis.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

//    @Transactional
//    public void saveTokenInfo(Long employeeId, String refreshToken, String accessToken) {
//        refreshTokenRepository.save(new RefreshToken(String.valueOf(employeeId), refreshToken, accessToken));
//    }

    @Transactional
    public void removeRefreshToken(String accessToken) {
        refreshTokenRepository.findByAccessToken(accessToken)
                .ifPresent(refreshToken -> refreshTokenRepository.delete(refreshToken));
    }
}
