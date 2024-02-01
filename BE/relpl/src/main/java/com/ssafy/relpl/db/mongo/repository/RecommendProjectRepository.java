package com.ssafy.relpl.db.mongo.repository;

import com.ssafy.relpl.db.mongo.entity.RecommendProject;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecommendProjectRepository extends MongoRepository<RecommendProject, Long> {

}
