package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {
    // 사용자 ID로 코인 조회
    List<Coin> findAllByUserUserId(Long userId);

    // 사용자 ID를 기준으로 코인 합산
    @Query(value = "SELECT COALESCE(SUM(c.coin_amount), 0) FROM Coin c WHERE c.user_id = :userId", nativeQuery = true)
    int sumCoinAmountByUserId(@Param("userId") Long userId);

}
