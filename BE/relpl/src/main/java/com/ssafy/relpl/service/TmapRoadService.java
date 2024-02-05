package com.ssafy.relpl.service;

import com.ssafy.relpl.db.mongo.entity.TmapRoad;
import com.ssafy.relpl.db.mongo.repository.TmapRoadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TmapRoadService {

    private final TmapRoadRepository tmapRoadRepository;
    public List<TmapRoad> getAllTmapRoadById(List<Long> roadHashIdList) {
        return tmapRoadRepository.findAllByroadHash(roadHashIdList);
    }

}