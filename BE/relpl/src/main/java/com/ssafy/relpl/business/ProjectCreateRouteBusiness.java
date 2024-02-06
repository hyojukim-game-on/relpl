package com.ssafy.relpl.business;

import com.ssafy.relpl.config.GeomFactoryConfig;
import com.ssafy.relpl.db.mongo.entity.RecommendProject;
import com.ssafy.relpl.db.postgre.entity.Project;
import com.ssafy.relpl.dto.request.ProjectCreateDistanceRequest;
import com.ssafy.relpl.dto.request.ProjectCreateRouteRequest;
import com.ssafy.relpl.dto.response.ProjectCreateDistanceResponse;
import com.ssafy.relpl.dto.response.ProjectCreateRouteResponse;
import com.ssafy.relpl.service.ProjectService;
import com.ssafy.relpl.service.RecommendProjectService;
import com.ssafy.relpl.service.ResponseService;
import com.ssafy.relpl.service.TmapRoadService;
import com.ssafy.relpl.util.annotation.Business;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Business
@RequiredArgsConstructor
public class ProjectCreateRouteBusiness {
    private final ProjectService projectService;
    private final RecommendProjectService recommendProjectService;
    private final GeomFactoryConfig geomFactoryConfig;
    private final ResponseService responseService;

    public ResponseEntity<?> createRouteProjectBusiness(ProjectCreateRouteRequest request) {
        try {
            Point startPoint = geomFactoryConfig.getGeometryFactory().createPoint(new Coordinate(request.getProjectStartCoordinate().getX(), request.getProjectStartCoordinate().getY()));
            Point endPoint = geomFactoryConfig.getGeometryFactory().createPoint(new Coordinate(request.getProjectEndCoordinate().getX(), request.getProjectEndCoordinate().getY()));
            Project project = Project.createRouteProject(request, startPoint, endPoint);
            project = projectService.createRouteProject(project);
            Optional<RecommendProject> mongoResult = recommendProjectService.getRecommendProjectById(request.getProjectSelectedId());
            if (mongoResult.isPresent()) {
                RecommendProject recommendProject = mongoResult.get();
                recommendProject.setProjectId(project.getProjectId());
                recommendProjectService.updateRecommendProject(recommendProject); // 동일한 id값이 있을 경우 mongoDB는 update
                return ResponseEntity.ok(responseService.getSingleResult(ProjectCreateRouteResponse.createProjectCreateRouteResponse(project)));
            } else {
                return ResponseEntity.badRequest().body(responseService.getFailResult(400, "경로 프로젝트가 존재하지 않음"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "경로 프로젝트 생성 실패"));
        }
    }
}
