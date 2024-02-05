package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.UserRoute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRouteRepository extends JpaRepository<UserRoute, Long> {
}
