package com.ssafy.relpl.service;

import org.springframework.data.geo.Point;
import com.ssafy.relpl.db.mongo.entity.RecommendProject;
import com.ssafy.relpl.db.mongo.repository.RecommendProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendProjectService {
    private final RecommendProjectRepository recommendProjectRepository;
    public RecommendProject saveRecommendProject(List<Point> recommendPaths, long totalDistance, long isShortest) {
        return recommendProjectRepository.save(RecommendProject.createRecommendProject(recommendPaths, totalDistance, isShortest));
    }
}
