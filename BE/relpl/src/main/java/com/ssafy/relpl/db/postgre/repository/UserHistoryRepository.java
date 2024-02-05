package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.Project;
import com.ssafy.relpl.db.postgre.entity.UserRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserHistoryRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p WHERE p.userId = :userId")
    List<Project> findProjectByUserId(Long userId);
}
