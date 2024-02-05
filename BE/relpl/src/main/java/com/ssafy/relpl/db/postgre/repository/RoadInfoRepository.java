package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.RoadInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoadInfoRepository extends JpaRepository<RoadInfo, Long> {
    @NotNull
    @Override
    List<RoadInfo> findAll();

    Optional<RoadInfo> findByRoadHashId(long roadHashId);
}
