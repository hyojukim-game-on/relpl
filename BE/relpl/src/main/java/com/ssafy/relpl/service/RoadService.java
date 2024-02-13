package com.ssafy.relpl.service;

import com.ssafy.relpl.dto.Road;
import com.ssafy.relpl.dto.RoadInfoRequest;
import com.ssafy.relpl.db.repository.mongoDB.RoadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoadService {
    @Autowired
    RoadRepository roadRepository;

    public Road save(RoadInfoRequest request) {

        Road road = Road.createRoad(request);
        Road saved = roadRepository.save(road);

        return saved;
    }
}
