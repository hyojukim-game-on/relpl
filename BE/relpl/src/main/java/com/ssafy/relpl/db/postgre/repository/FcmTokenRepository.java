package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByUserId(Long userId);
    Optional<FcmToken> findByFcmToken(String fcmToken);
}