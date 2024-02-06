package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.UserRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRouteRepository extends JpaRepository<UserRoute, Long> {
    UserRoute findLatestUserRouteByUserIdAndProjectId(Long userId, Long projectId);

    UserRoute findTopByUserIdAndProjectIdAndUserMoveEnd(Long userId, Long projectId, String userMoveEnd);

    List<UserRoute> findByProjectId(Long projectId);
//    List<Long> findDistinctUserIdsByProjectId(Long projectId);
}
