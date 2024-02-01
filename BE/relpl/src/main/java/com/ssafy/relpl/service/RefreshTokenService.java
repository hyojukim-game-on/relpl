package com.ssafy.relpl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
//    private final RefreshTokenRepository refreshTokenRepository;

//    @Transactional
//    public void saveTokenInfo(Long employeeId, String refreshToken, String accessToken) {
//        refreshTokenRepository.save(new RefreshToken(String.valueOf(employeeId), refreshToken, accessToken));
//    }

//    @Transactional
//    public void removeRefreshToken(String accessToken) {
//        refreshTokenRepository.findByAccessToken(accessToken)
//                .ifPresent(refreshToken -> refreshTokenRepository.delete(refreshToken));
//    }
}
