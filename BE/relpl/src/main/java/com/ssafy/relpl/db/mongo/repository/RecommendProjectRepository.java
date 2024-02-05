package com.ssafy.relpl.db.mongo.repository;

import com.ssafy.relpl.db.mongo.entity.RecommendProject;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface RecommendProjectRepository extends MongoRepository<RecommendProject, Long> {

}
