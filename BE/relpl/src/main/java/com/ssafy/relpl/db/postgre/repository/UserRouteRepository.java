package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.UserRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRouteRepository extends JpaRepository<UserRoute, Long> {

    // 해당 userId 의 userMoveDistance 모두 더해주기
    @Query("SELECT SUM(ur.userMoveDistance) FROM UserRoute ur WHERE ur.userId = :userId")
    int sumUserMoveDistanceByUserId(Long userId);

    // 해당 userId 의 userMoveTime 모두 더해주기
    @Query("SELECT SUM(ur.userMoveTime) FROM UserRoute ur WHERE ur.userId = :userId")
    int sumUserMoveTimeByUserId(Long userId);

    // projectId 를 갖는 모든 userRoute 조회
    List<UserRoute> findByProjectId(Long projectId);

}