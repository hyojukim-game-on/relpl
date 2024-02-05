package com.ssafy.relpl.db.mongo.repository;

import com.ssafy.relpl.db.mongo.entity.RecommendProject;
import org.springframework.data.mongodb.repository.MongoRepository;

public class UserRouteRepository extends MongoRepository<RecommendProject, Long> {
}
