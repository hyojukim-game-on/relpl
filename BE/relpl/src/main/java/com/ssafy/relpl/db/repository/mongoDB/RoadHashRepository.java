package com.ssafy.relpl.db.repository.mongoDB;


import com.ssafy.relpl.dto.RoadHash;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoadHashRepository extends MongoRepository<RoadHash, String> {
}
