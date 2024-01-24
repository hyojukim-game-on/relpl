package com.ssafy.relpl.db.redis.repository;


import com.ssafy.relpl.dto.response.RankingEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingRepository extends CrudRepository<RankingEntry, String> {
    // 필요한 조회 메서드 정의
}