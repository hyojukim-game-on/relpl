package com.ssafy.relpl.service;

import com.ssafy.relpl.db.postgre.entity.RoadHash;
import com.ssafy.relpl.db.postgre.repository.RoadHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadHashService {

    private final RoadHashRepository roadHashRepository;
    public List<RoadHash> getAllRoadHash() {
        return roadHashRepository.findAll();
    }
}