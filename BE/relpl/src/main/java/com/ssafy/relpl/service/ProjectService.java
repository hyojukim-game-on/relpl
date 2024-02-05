package com.ssafy.relpl.service;

import com.ssafy.relpl.config.GeomFactoryConfig;
import com.ssafy.relpl.db.postgre.entity.Project;
import com.ssafy.relpl.db.postgre.repository.ProjectRepository;
import com.ssafy.relpl.dto.request.ProjectCreateDistanceRequest;
import com.ssafy.relpl.dto.request.ProjectDistanceLookupRequest;
import com.ssafy.relpl.dto.request.ProjectJoinRequest;
import com.ssafy.relpl.dto.request.ProjectRouteLookupRequest;
import com.ssafy.relpl.dto.response.ProjectCreateDistanceResponse;
import com.ssafy.relpl.dto.response.ProjectDistanceLookupResponse;
import com.ssafy.relpl.dto.response.ProjectExistResponse;
import com.ssafy.relpl.dto.response.ProjectRouteLookupResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
            ProjectCreateDistanceResponse response = ProjectCreateDistanceResponse.builder()
                    .projectId(project.getProjectId())
                    .build();
            return ResponseEntity.ok(responseService.getSingleResult(response, "거리 프로젝트 생성 완료", 200));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "거리 프로젝트 생성 실패"));
        }
    }

    @Transactional
    public ResponseEntity<?> join(ProjectJoinRequest request) {
        log.info("project join");

        //프로젝트 조회하기
        Optional<Project> projectOptional = projectRepository.findById(request.getProjectId());

        //프로젝트가 존재하는지 확인
        if(projectOptional.isPresent()) {
            Project project = projectOptional.get();

            //프로젝트가 종료되지 않고, 누군가 참여중이지 않는지 확인
            if(project.isProjectIsDone() == false && project.isProjectIsPlogging() == false) {
                project.setProjectIsPlogging(true);
                return ResponseEntity.ok(responseService.getSingleResult(true, "프로젝트 참여 성공", 200));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 참여 실패"));
    }

    // 거리기반 릴레이 상세 정보 조회 로직
    public ResponseEntity<?> lookupDistance(ProjectDistanceLookupRequest request) {
        try {
            // 프로젝트 조회
            Optional<Project> projectOptional = projectRepository.findById(request.getProjectId());

            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();

                // 프로젝트 정보를 ProjectDistanceLookupResponse로 매핑
                ProjectDistanceLookupResponse response = ProjectDistanceLookupResponse.builder()
                        .projectId(project.getProjectId())
                        .projectName(project.getProjectName())
                        .projectTotalContributer(project.getProjectTotalContributer())
                        .projectTotalDistance(project.getProjectTotalDistance())
                        .projectRemainingDistance(project.getProjectRemainingDistance())
                        .projectCreateDate(project.getProjectCreateDate())
                        .projectEndDate(project.getProjectEndDate())
                        .projectIsPath(project.isProjectIsPath())
                        .projectStopCoordinate(project.getProjectStopCoordinate())
                        .build();

                // 여기서 진행률 계산 로직 추가
                // response.setProgress(계산한_진행률);

                // userRoute entity에서 userMoveMemo 및 userMoveImage 정보를 가져와서 설정하는 로직 추가
                // response.setUserMoveMemo(가져온_userMoveMemo);
                // response.setUserMoveImage(가져온_userMoveImage);

                return ResponseEntity.ok(responseService.getSingleResult(response, "거리기반 릴레이 상세 정보 조회 성공", 200));
            } else {
                return ResponseEntity.badRequest().body(responseService.getFailResult(400, "프로젝트가 존재하지 않습니다."));
            }
        } catch (Exception e) {
            log.error("거리기반 릴레이 상세 정보 조회 에러", e);
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "거리기반 릴레이 상세 정보 조회 실패"));
        }
    }

    // 경로 기반 릴레이 상세 정보 조호 로직
    public ResponseEntity<?> lookupRoute(ProjectRouteLookupRequest request) {
        try {
            // 프로젝트 조회
            Optional<Project> projectOptional = projectRepository.findById(request.getProjectId());

            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();

                // 프로젝트 정보를 ProjectRouteLookupResponse로 매핑
                ProjectRouteLookupResponse response = ProjectRouteLookupResponse.builder()
                        .projectId(project.getProjectId())
                        .projectName(project.getProjectName())
                        .projectTotalContributer(project.getProjectTotalContributer())
                        .projectTotalDistance(project.getProjectTotalDistance())
                        .projectRemainingDistance(project.getProjectRemainingDistance())
                        .projectCreateDate(project.getProjectCreateDate())
                        .projectEndDate(project.getProjectEndDate())
                        .projectIsPath(project.isProjectIsPath())
                        .projectStopCoordinate(project.getProjectStopCoordinate())
                        .build();

                // 여기서 진행률 계산 로직 추가
                // response.setProgress(계산한_진행률);

                // userRoute entity에서 userMoveMemo 및 userMoveImage 정보를 가져와서 설정하는 로직 추가
                // response.setUserMoveMemo(가져온_userMoveMemo);
                // response.setUserMoveImage(가져온_userMoveImage);

                // mongodb recommendProject에서 recommendLineString 정보를 가져와서 설정하는 로직 추가
                // response.setRecommendLineString(가져온_recommendLineString);

                return ResponseEntity.ok(responseService.getSingleResult(response, "경로기반 릴레이 상세 정보 조회 성공", 200));
            } else {
                return ResponseEntity.badRequest().body(responseService.getFailResult(400, "프로젝트가 존재하지 않습니다."));
            }
        } catch (Exception e) {
            log.error("경로기반 릴레이 상세 정보 조회 에러", e);
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "경로기반 릴레이 상세 정보 조회 실패"));
        }
    }
}

