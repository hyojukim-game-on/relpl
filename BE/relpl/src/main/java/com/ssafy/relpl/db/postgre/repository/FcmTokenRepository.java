package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    FcmToken findByUserUserId(Long userId);
}