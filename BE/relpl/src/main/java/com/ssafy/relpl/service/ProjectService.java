package com.ssafy.relpl.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssafy.relpl.config.GeomFactoryConfig;
import com.ssafy.relpl.db.postgre.entity.Project;
import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.entity.UserRoute;
import com.ssafy.relpl.db.postgre.repository.ProjectRepository;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.request.ProjectCreateDistanceRequest;
import com.ssafy.relpl.dto.request.ProjectCreateRouteRequest;
import com.ssafy.relpl.dto.request.ProjectJoinRequest;
import com.ssafy.relpl.dto.response.ProjectAllResponse;
import com.ssafy.relpl.dto.request.ProjectStopRequest;
import com.ssafy.relpl.dto.response.ProjectCreateDistanceResponse;
import com.ssafy.relpl.dto.response.ProjectExistResponse;
import com.ssafy.relpl.dto.response.TmapApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ResponseService responseService;
    private final GeomFactoryConfig geomFactoryConfig;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.base-url}")
    private String baseUrl;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
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

    @Transactional
    public ResponseEntity<?> createDistanceProject(ProjectCreateDistanceRequest request) {
        try {
            Point startPoint = geomFactoryConfig.getGeometryFactory().createPoint(new Coordinate(request.getProjectStartCoordinate().getX(), request.getProjectStartCoordinate().getY()));
            Project project = Project.createDistanceProject(request, startPoint);
            projectRepository.save(project);
            ProjectCreateDistanceResponse response = ProjectCreateDistanceResponse.createProjectCreateDistanceResponse(project);
            return ResponseEntity.ok(responseService.getSingleResult(response, "거리 프로젝트 생성 완료", 200));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "거리 프로젝트 생성 실패"));
        }
    }

    public Project createRouteProject(Project project) {
        try {
            return projectRepository.save(project);
        } catch (Exception e) {
            log.error("경로 프로젝트 생성 실패", e);
            return null;
        }
    }

    @Transactional
    public ResponseEntity<?> join(ProjectJoinRequest request) {
        log.info("project join");

        //프로젝트 조회하기
        Optional<Project> projectOptional = projectRepository.findById(request.getProjectId());

        //프로젝트가 존재하는지 확인
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();

            //프로젝트가 종료되지 않고, 누군가 참여중이지 않는지 확인
            if (project.isProjectIsDone() == false && project.isProjectIsPlogging() == false) {
                project.setProjectIsPlogging(true);
                return ResponseEntity.ok(responseService.getSingleResult(true, "프로젝트 참여 성공", 200));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 참여 실패"));
    }


    public ResponseEntity<?> getAllProjectList() {

        try {
            List<Project> projectList = projectRepository.findAll();
            List<ProjectAllResponse> response = new ArrayList<>();
            for (Project project : projectList) {
                if (project.isProjectIsDone()) continue;
                response.add(ProjectAllResponse.createProjectAllResponse(project));
            }
            return ResponseEntity.ok(responseService.getSingleResult(response, "프로젝트 전체 조회 성공", 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 전체 조회 실패 실패"));
        }
    }

    @Transactional
    public ResponseEntity<?> stop(ProjectStopRequest request) throws ExecutionException, InterruptedException {
        log.info("project stop");

        // 플로깅 이미지가 없다면 에러
        if (request.getMoveImage().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 중단 실패 : image 없음"));
        }
        // 플로깅 이미지를 s3에 업로드 후 받은 url
        String resultUrl = setProjectStopProfilePhoto(request.getMoveImage());

        // 플로깅 이미지 저장 후
        if (resultUrl != null) {
            log.info("플로깅 사진 s3에 업로드 성공");
            Optional<User> userOptional = userRepository.findById(request.getUserId());

            //유저 조회
            if(userOptional.isPresent()) {
                User user = userOptional.get();
                Optional<Project> projectOptional = projectRepository.findById(request.getProjectId());

                //프로젝트 조회
                if(projectOptional.isPresent()) {
                    Project project = projectOptional.get();

                    //프로젝트가 종료되지 않았고, 누군가 참여중인지 확인
                    if(!project.isProjectIsDone() && project.isProjectIsPlogging()) {

                        //가장 가까운 도로 id 찾아서 map 에 넣기 (tmap api 호출) wait
                        HashMap<String, Integer> map = performAsyncTasks(request.getUserMovePath());

                        //map 의 key 를 redis 에 저장 (road_{tmapid})
                        addRoadIntoRedis(map);

                        //도로를 mongoDB 에 저장 (키 반환)
                        String routeKey = "";

                        // UserRoute 객체 생성 및 저장
                        UserRoute userRoute = UserRoute.builder()
                                .userId(request.getUserId())
                                .projectId(request.getProjectId())
                                .userNickname(request.getUserNickname())
                                .projectName(request.getProjectName())
                                .userMoveStart(request.getMoveStart())
                                .userMoveEnd(request.getMoveEnd())
                                .userMoveDistance(request.getMoveDistance())
                                .userMoveTime(request.getMoveTime())
                                .userMovePath(routeKey)
                                .userMoveMemo(request.getMoveMemo())
                                .userMoveImage(resultUrl)
                                .build();
                    }
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 중단 실패: 프로젝트가 이미 종료되거나, 플로깅 중이지 않음"));
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 중단 실패: 프로젝트가 존재하지 않음"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 중단 실패: 유저가 존재하지 않음"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 중단 실패: s3 이미지 저장 실패"));
    }

    private void addRoadIntoRedis(HashMap<String, Integer> map) {
        // redis에 저장
//        redisTemplate.opsForValue().set(
//                "token_"+userId,
//                refreshToken,
//                refreshExpirationTime,
//                TimeUnit.MILLISECONDS
//        );
    }

    @Async
    public CompletableFuture<TmapApiResponse> asyncMethod(Point point) {
        //tmap api 가까운 도로 호출
        TmapApiResponse response = null;

        return CompletableFuture.completedFuture(response);
    }

    // 모든 비동기 작업이 완료될 때까지 기다리는 메서드
    public void waitForAllTasks(List<Future<TmapApiResponse>> futures) throws ExecutionException, InterruptedException {
        for (Future<TmapApiResponse> future : futures) {
            future.get(); // 각 비동기 작업의 결과를 가져와서 처리
        }
    }

    // 비동기 작업들을 수행하고 모든 작업이 완료될 때까지 기다리는 메서드
    public HashMap<String, Integer> performAsyncTasks(List<Point> coordintates) throws ExecutionException, InterruptedException {
        log.info("Tmap api 호출");

        List<Future<TmapApiResponse>> futures = new ArrayList<>();


        // 비동기 메서드 호출 및 Future 객체 저장
        for (Point point: coordintates) {
            futures.add(asyncMethod(point).toCompletableFuture());
        }

        // 모든 비동기 작업이 완료될 때까지 기다림
        waitForAllTasks(futures);
    }

    /* setUserProfilePhoto : s3에 프로젝트 중단 시 사진 업로드하기
    @param : 프로젝트 중단 시 제공한 사진 파일
    @return : 성공 시) uploadedFileUrl s3에 업로드 한 프로필 사진 url , 실패 시) null
    * */
    public String setProjectStopProfilePhoto(MultipartFile file) {
        try{

            // 유저가 제공한 프로필 사진 파일의 원래 이름
            String originalFileName = file.getOriginalFilename();

            // 중복 방지를 위한 랜덤 값 String 을 앞에 추가
            String s3UploadFileName = UUID.randomUUID() + originalFileName;

            // S3에 업로드 된 파일의 url 주소..
            String uploadedFileUrl = baseUrl + s3UploadFileName;

            // S3에 파일과 함께 올릴 메타데이터
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // S3에 요청 보낼 객체 생성
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucket, s3UploadFileName, file.getInputStream(), metadata
            );

            // S3에 요청 보내서 파일 업로드
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3Client.putObject(putObjectRequest);

            log.info("s3에 프로필 사진 파일 업로드 성공");

            return uploadedFileUrl;
        } // S3에 파일 업로드 실패 시 에러 출력하고 null 리턴
        catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

}
