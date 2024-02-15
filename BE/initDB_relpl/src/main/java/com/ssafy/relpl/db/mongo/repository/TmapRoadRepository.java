package com.ssafy.relpl.db.mongo.repository;


import com.ssafy.relpl.db.mongo.entity.TmapRoad;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TmapRoadRepository extends MongoRepository<TmapRoad, String> {
    //TODO

    @Override
    <S extends TmapRoad> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);
}
