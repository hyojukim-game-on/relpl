package com.ssafy.relpl.db.postgre.repository;


import com.ssafy.relpl.db.postgre.entity.PointHash;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHashRepository extends JpaRepository<PointHash, Long> {
    @Override
    <S extends PointHash> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);
}
