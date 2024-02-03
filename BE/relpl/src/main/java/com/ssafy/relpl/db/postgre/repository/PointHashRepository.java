package com.ssafy.relpl.db.postgre.repository;


import com.ssafy.relpl.db.postgre.entity.PointHash;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface PointHashRepository extends JpaRepository<PointHash, Long>, QueryByExampleExecutor<PointHash>{
    @Override
    <S extends PointHash> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);

    @Query(value = "SELECT COUNT(*) FROM pointhash", nativeQuery = true)
    int countAllPointHash();
    @Query(value =
            "SELECT *\n" +
            "FROM\n" +
            "    pointhash\n" +
            "ORDER BY\n" +
            "    ST_Distance(pointhash.point_coordinate , ST_SetSRID(ST_MakePoint(:x, :y), 4326), true)\n" +
            "LIMIT 1;"
            , nativeQuery = true)
    PointHash findFirstByPointCoordinate(@Param("x") double x, @Param("y") double y);
}
