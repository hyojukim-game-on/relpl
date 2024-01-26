package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {
    // 사용자 ID로 코인 조회
    List<Coin> findAllByUserId(Long userId);

}
