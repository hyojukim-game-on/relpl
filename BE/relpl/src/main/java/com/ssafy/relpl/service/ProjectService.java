package com.ssafy.relpl.service;

import com.ssafy.relpl.config.GeomFactoryConfig;
import com.ssafy.relpl.db.postgre.entity.Project;
import com.ssafy.relpl.db.postgre.repository.ProjectRepository;
import com.ssafy.relpl.dto.request.ProjectCreateDistanceRequest;
import com.ssafy.relpl.dto.response.ProjectExistResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ResponseService responseService;
    private final GeomFactoryConfig geomFactoryConfig;

    public ResponseEntity<?> projectExist(double x, double y, int distance) {
        try {
            List<Project> result = projectRepository.findNearProject10(x, y, distance);
            if (result.isEmpty())
                return ResponseEntity.ok(responseService.getSingleResult(ProjectExistResponse.createProjectNotExistDto(), "50m 근처 프로젝트 존재하지 않음", 200));
            else
                return ResponseEntity.ok(responseService.getSingleResult(ProjectExistResponse.createProjectExistDto(result.get(0)), "50m 근처 프로젝트 존재", 200));
        } catch (Exception e) {
            log.error("isProjectExist error", e);
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "50m 근처 프로젝트 조회 실패"));
        }

    }

    public ResponseEntity<?> createDistanceProject(ProjectCreateDistanceRequest request) {
        try {
            Point startPoint = geomFactoryConfig.getGeometryFactory().createPoint(new Coordinate(request.getProjectStartPoint().getX(), request.getProjectStartPoint().getY()));
            Project project = Project.builder()
                    .userId(request.getUserId())
                    .projectName(request.getProjectName())
                    .projectCreateDate(request.getProjectCreateDate())
                    .projectEndDate(request.getProjectEndDate())
                    .projectStartCoordinate(startPoint)
                    .projectStopCoordinate(startPoint)
                    .projectEndCoordinate(null)
                    .projectIsPath(false)
                    .projectRemainingDistance(request.getProjectTotalDistance())
                    .projectTotalDistance(request.getProjectTotalDistance())
                    .projectIsDone(false)
                    .projectIsPlogging(true)
                    .projectTotalContributer(1)
                    .build();
            project = projectRepository.save(project);
            return ResponseEntity.ok(responseService.getSingleResult(project.getProjectId(), "거리 프로젝트 생성 완료", 200));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "거리 프로젝트 생성 실패"));
        }
    }
}
