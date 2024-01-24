package com.ssafy.relpl.db.mongo.repository;


import com.ssafy.relpl.db.mongo.entity.RoadHash;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoadHashRepository extends MongoRepository<RoadHash, String> {
    //TODO
}
