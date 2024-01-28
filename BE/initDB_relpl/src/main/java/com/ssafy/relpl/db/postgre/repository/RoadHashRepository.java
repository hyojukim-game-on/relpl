package com.ssafy.relpl.db.postgre.repository;



import com.ssafy.relpl.db.postgre.entity.RoadHash;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadHashRepository extends JpaRepository<RoadHash, Long> {
    @Override
    <S extends RoadHash> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);
}
