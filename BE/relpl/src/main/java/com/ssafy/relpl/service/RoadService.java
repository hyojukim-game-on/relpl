package com.ssafy.relpl.service;

import com.ssafy.relpl.db.mongo.repository.RoadRepository;
import com.ssafy.relpl.dto.request.SampleRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoadService {
    @Autowired
    RoadRepository roadRepository;

    public SampleRequestDto.Road save(SampleRequestDto.RoadInfoRequest request) {

        SampleRequestDto.Road road = SampleRequestDto.Road.createRoad(request);
        SampleRequestDto.Road saved = roadRepository.save(road);

        return saved;
    }
}
