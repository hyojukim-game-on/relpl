package com.ssafy.relpl.repository;


import com.ssafy.relpl.dto.Road;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoadRepository extends MongoRepository<Road, String> {
}
