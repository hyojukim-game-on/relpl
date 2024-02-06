package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.UserRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRouteRepository extends JpaRepository<UserRoute, Long> {
    Optional<UserRoute> findLatestUserRouteByUserIdAndProjectId(Long userId, Long projectId);

    Optional<UserRoute> findTopByUserIdAndProjectIdAndUserMoveEnd(Long userId, Long projectId, String userMoveEnd);
}

