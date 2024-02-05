package com.ssafy.relpl.business;

import com.ssafy.relpl.config.GeomFactoryConfig;
import com.ssafy.relpl.db.postgre.entity.Project;
import com.ssafy.relpl.dto.request.ProjectCreateDistanceRequest;
import com.ssafy.relpl.dto.request.ProjectCreateRouteRequest;
import com.ssafy.relpl.dto.response.ProjectCreateDistanceResponse;
import com.ssafy.relpl.service.ProjectService;
import com.ssafy.relpl.service.TmapRoadService;
import com.ssafy.relpl.util.annotation.Business;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.springframework.http.ResponseEntity;

@Business
@RequiredArgsConstructor
public class ProjectCreateRouteBusiness {
    private final ProjectService projectService;
    private final TmapRoadService tmapRoadService;
    private final GeomFactoryConfig geomFactoryConfig;

    @Transactional
    public void createDistanceProject(ProjectCreateRouteRequest request) {
        try {
            Point startPoint = geomFactoryConfig.getGeometryFactory().createPoint(new Coordinate(request.getProjectStartPoint().getX(), request.getProjectStartPoint().getY()));
            Point endPoint = geomFactoryConfig.getGeometryFactory().createPoint(new Coordinate(request.getProjectEndPoint().getX(), request.getProjectEndPoint().getY()));
            tmapRoadService.
            Project project = Project.createRouteProject(request, startPoint, endPoint, 10);
            projectRepository.save(project);
            ProjectCreateDistanceResponse response = ProjectCreateDistanceResponse.createProjectCreateDistanceResponse(project);
            return ResponseEntity.ok(responseService.getSingleResult(response, "거리 프로젝트 생성 완료", 200));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "거리 프로젝트 생성 실패"));
        }
    }
}
