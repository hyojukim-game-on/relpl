package com.ssafy.relpl.service;

import com.ssafy.relpl.db.mongo.entity.RecommendProject;
import com.ssafy.relpl.db.mongo.repository.RecommendProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendProjectService {
    private final RecommendProjectRepository recommendProjectRepository;
    public RecommendProject saveRecommendProject(List<Point> recommendPaths, long totalDistance, long isShortest) {
        return recommendProjectRepository.save(RecommendProject.createRecommendProject(recommendPaths, totalDistance, isShortest));
    }

    public Optional<RecommendProject> getRecommendProjectById(String id) {
        return recommendProjectRepository.findById(id);
    }

    public RecommendProject updateRecommendProject(RecommendProject project) {
        return recommendProjectRepository.save(project); // 동일한 id값이 있을 경우 mongoDB는 update
    }
}
