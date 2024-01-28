package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.RoadInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadInfoRepository extends JpaRepository<RoadInfo, Long> {
    @Override
    <S extends RoadInfo> List<S> saveAll(Iterable<S> entities);
}
