package com.ssafy.relpl.db.mongo.repository;
import com.ssafy.relpl.db.mongo.entity.UserRouteDetail;
import com.ssafy.relpl.db.postgre.entity.UserRoute;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRouteDetailRepository extends MongoRepository<UserRouteDetail, String> {
    // projectId 를 갖는 모든 userRouteDetail 조회
    List<UserRouteDetail> findByProjectId(Long projectId);
}