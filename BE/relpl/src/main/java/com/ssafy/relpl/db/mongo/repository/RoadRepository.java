package com.ssafy.relpl.db.mongo.repository;


import com.ssafy.relpl.db.mongo.entity.Road;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoadRepository extends MongoRepository<Road, String> {
    //TODO
}
