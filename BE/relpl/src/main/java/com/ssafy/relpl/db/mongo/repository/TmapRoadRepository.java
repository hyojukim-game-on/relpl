package com.ssafy.relpl.db.mongo.repository;

import com.ssafy.relpl.db.mongo.entity.TmapRoad;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;


public interface TmapRoadRepository extends MongoRepository<TmapRoad, Long> {
    @Query("{'roadHash': {$in: ?0}}")
    List<TmapRoad> findAllByroadHash(List<Long> tmapIds);
}
