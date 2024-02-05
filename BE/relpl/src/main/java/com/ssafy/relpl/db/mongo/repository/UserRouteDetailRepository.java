package com.ssafy.relpl.db.mongo.repository;

import com.ssafy.relpl.db.mongo.entity.UserRouteDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRouteDetailRepository extends MongoRepository<UserRouteDetail, String> {
}
