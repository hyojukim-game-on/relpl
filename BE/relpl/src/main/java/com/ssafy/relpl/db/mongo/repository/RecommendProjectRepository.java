package com.ssafy.relpl.db.mongo.repository;

import com.ssafy.relpl.db.mongo.entity.RecommendProject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecommendProjectRepository extends MongoRepository<RecommendProject, Long> {


    @Query("{'_id':?0}")
    Optional<RecommendProject> findById(String _id);

    Optional<RecommendProject> findByProjectId(Long projectId);
}
