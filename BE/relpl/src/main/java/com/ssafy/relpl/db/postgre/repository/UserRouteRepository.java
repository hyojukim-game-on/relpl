package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.UserRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRouteRepository extends JpaRepository<UserRoute, Long> {

    // 해당 userId 의 userMoveDistance 모두 더해주기
    @Query("SELECT COALESCE(SUM(ur.userMoveDistance), 0) FROM UserRoute ur WHERE ur.userId = :userId")
    int sumUserMoveDistanceByUserId(@Param("userId") Long userId);

    // 해당 userId 의 userMoveTime 모두 더해주기
    @Query("SELECT COALESCE(SUM(ur.userMoveDistance), 0) FROM UserRoute ur WHERE ur.userId = :userId")
    int sumUserMoveTimeByUserId(Long userId);

    List<UserRoute> findByProjectId(Long projectId);
    List<UserRoute> findByUserId(Long userId);
    List<UserRoute> findByUserIdAndProjectIdOrderByUserMoveIdDesc(Long userId, Long projectId);

    // projectId로 조회할 때 중복 없이 userId 조회
    List<UserRoute> findDistinctUserIdByProjectId(Long projectId);

}