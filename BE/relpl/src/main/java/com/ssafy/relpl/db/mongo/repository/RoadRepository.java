package com.ssafy.relpl.db.mongo.repository;


import com.ssafy.relpl.dto.request.SampleRequestDto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoadRepository extends MongoRepository<SampleRequestDto.Road, String> {
}
