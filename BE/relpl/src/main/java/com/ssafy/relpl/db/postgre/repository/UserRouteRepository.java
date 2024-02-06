package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.UserRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRouteRepository extends JpaRepository<UserRoute, Long> {
    List<UserRoute> findByUserIdAndProjectIdOrderByUserMoveIdDesc(Long userId, Long projectId);
}
