package com.ssafy.relpl.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssafy.relpl.config.GeomFactoryConfig;
import com.ssafy.relpl.db.mongo.entity.RecommendProject;
import com.ssafy.relpl.db.mongo.repository.RecommendProjectRepository;
import com.ssafy.relpl.db.mongo.entity.UserRouteDetail;
import com.ssafy.relpl.db.mongo.repository.UserRouteDetailRepository;
import com.ssafy.relpl.db.postgre.entity.*;
import com.ssafy.relpl.db.postgre.repository.*;
import com.ssafy.relpl.dto.request.*;
import com.ssafy.relpl.dto.response.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserRouteRepository userRouteRepository;
    private final UserRouteDetailRepository userRouteDetailRepository;
    private final RecommendProjectRepository recommendProjectRepository;
    private final CoinRepository coinRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final TmapService tmapService;
    private final ResponseService responseService;
    private final FcmTokenService fcmTokenService;
    private final RankingService rankingService;
    private final RedisTemplate<String, String> redisTemplate;
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
                log.info("프로젝트 참여 완료");
                project.setProjectIsPlogging(true);
                return ResponseEntity.ok(responseService.getSingleResult(ProjectJoinResponse.createProjectJoinResponse(project.getProjectId()), "프로젝트 참여 성공", 200));
            }
        }
        log.info("프로젝트 참여 실패");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 참여 실패"));
    }


    @Transactional
    public ResponseEntity<?> getAllProjectList() {

        try {
            List<Project> projectList = projectRepository.findAll();
            List<ProjectAllResponse> response = new ArrayList<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate now = LocalDate.now();
             // 안되면 취소하기
            for (Project project : projectList) {
                if (project.isProjectIsDone() || project.isProjectIsPlogging()) continue;
                LocalDate projectEndDateTime = LocalDate.parse(project.getProjectEndDate(), formatter);
                if (now.isAfter(projectEndDateTime)) {
                    project.setProjectCoordinateCurrentSize(-1);
                    continue;
                }
                response.add(ProjectAllResponse.createProjectAllResponse(project));
            }
            return ResponseEntity.ok(responseService.getSingleResult(response, "프로젝트 전체 조회 성공", 200));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 전체 조회 실패 실패"));
        }
    }

    // 거리기반 릴레이 상세 정보 조회 로직
    public ResponseEntity<?> lookupDistance(ProjectDistanceLookupRequest request) {
        try {
            // 해당 프로젝트 조회
            Optional<Project> projectOptional = projectRepository.findById(request.getProjectId());

            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();
                org.springframework.data.geo.Point convertPoint = new org.springframework.data.geo.Point(project.getProjectStopCoordinate().getX(), project.getProjectStopCoordinate().getY());
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
                        .projectStopCoordinate(convertPoint)
                        .progress(project.calculateProgress())  // 여기서 calculateProgress 메서드 호출
                        .build();

                setDistanceUserMoveDetails(project, response);

                return ResponseEntity.ok(responseService.getSingleResult(response, "거리기반 릴레이 상세 정보 조회 성공", 200));
            } else {
                return ResponseEntity.badRequest().body(responseService.getFailResult(400, "프로젝트가 존재하지 않습니다."));
            }
        } catch (Exception e) {
            log.error("거리기반 릴레이 상세 정보 조회 에러", e);
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "거리기반 릴레이 상세 정보 조회 실패"));
        }
    }

    // userMoveMemo 및 userMoveImage 설정 로직
    private void setDistanceUserMoveDetails(Project project, ProjectDistanceLookupResponse response) {
        // 프로젝트에 해당하는 모든 사용자의 이동 정보 조회
        List<UserRoute> userRouteList = userRouteRepository.findByProjectId(project.getProjectId());

        // 사용자 이동 정보를 UserMoveEnd 시간을 기준으로 정렬
        Collections.sort(userRouteList, Comparator.comparing(UserRoute::getUserMoveEnd));

        // 가장 마지막에 등록된 사용자의 이동 정보를 가져옴
        if (!userRouteList.isEmpty()) {
            UserRoute lateUser = userRouteList.get(userRouteList.size() - 1);
            // 해당 사용자의 메모와 이미지를 response에 추가
            response.setUserMoveMemo(lateUser.getUserMoveMemo());
            response.setUserMoveImage(lateUser.getUserMoveImage());
        }
    }



    // 경로 기반 릴레이 상세 정보 조회 로직
    public ResponseEntity<?> lookupRoute(ProjectRouteLookupRequest request) {
        try {
            // 해당 프로젝트 조회
            Optional<Project> projectOptional = projectRepository.findById(request.getProjectId());

            if (projectOptional.isPresent()) {
                Project project = projectOptional.get();

                Optional<RecommendProject> recommendProject = recommendProjectRepository.findByProjectId(request.getProjectId());
                List<org.springframework.data.geo.Point> convertCurrentProjectPointList = new ArrayList<>();
                if (recommendProject.isPresent()) {
                    RecommendProject currentProject = recommendProject.get();
                    List<org.springframework.data.geo.Point> currentProjectPointList = currentProject.getRecommendLineString().getCoordinates();
                    for (org.springframework.data.geo.Point p : currentProjectPointList) {
                        convertCurrentProjectPointList.add(new org.springframework.data.geo.Point(p.getX(), p.getY()));
                    }
                } else {
                    log.error("프로젝트 전체 경로 조회 실패");
                    throw new Exception();
                }

                org.springframework.data.geo.Point convertPoint = new org.springframework.data.geo.Point(project.getProjectStopCoordinate().getX(), project.getProjectStopCoordinate().getY());
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
                        .projectStopCoordinate(convertPoint)
                        .progress(project.calculateProgress())  // 여기서 calculateProgress 메서드 호출
                        .projectRoute(convertCurrentProjectPointList)
                        .build();
                //추가된 로직 호출
                setRouteUserMoveDetails(project, response);

                log.info("추가된 로직 호출 완료");
                return ResponseEntity.ok(responseService.getSingleResult(response, "경로기반 릴레이 상세 정보 조회 성공", 200));
            } else {
                return ResponseEntity.badRequest().body(responseService.getFailResult(400, "프로젝트가 존재하지 않습니다."));
            }
        } catch (Exception e) {
            log.error("경로기반 릴레이 상세 정보 조회 에러", e);
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "경로기반 릴레이 상세 정보 조회 실패"));
        }
    }

    // setRouteUserMoveDetails 메서드 정의
    private void setRouteUserMoveDetails(Project project, ProjectRouteLookupResponse response) {
        // 프로젝트에 해당하는 모든 사용자의 이동 정보 조회
        List<UserRoute> userRouteList = userRouteRepository.findByProjectId(project.getProjectId());

        // 사용자 이동 정보를 UserMoveEnd 시간을 기준으로 정렬
        Collections.sort(userRouteList, Comparator.comparing(UserRoute::getUserMoveEnd));

        // 가장 마지막에 등록된 사용자의 이동 정보를 가져옴
        if (!userRouteList.isEmpty()) {
            UserRoute lateUser = userRouteList.get(userRouteList.size() - 1);
            // 해당 사용자의 메모와 이미지를 response에 추가
            response.setUserMoveMemo(lateUser.getUserMoveMemo());
            response.setUserMoveImage(lateUser.getUserMoveImage());
        }
    }

    @Transactional
    public ResponseEntity<?> stopPhoto(ProjectStopPhotoRequest request) {
        log.info("project stop photo");

        // 플로깅 이미지가 없다면 에러
        if (request.getMoveImage().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 중단 실패 : image 없음"));
        }
        // 플로깅 이미지를 s3에 업로드 후 받은 url
        String resultUrl = setProjectStopProfilePhoto(request.getMoveImage());

        // 플로깅 이미지 저장 후
        if (resultUrl != null) {
            log.info("플로깅 사진 s3에 업로드 성공");
            //해당 유저가 해당 프로젝트에 참여한 이력을 최신순으로 조회.
            List<UserRoute> userRouteList = userRouteRepository.findByUserIdAndProjectIdOrderByUserMoveIdDesc(request.getUserId(), request.getProjectId());

            //참여한 이력이 존재하는 경우
            if(userRouteList.size() > 0) {
                log.info("UserRoute 조회 성공");
                UserRoute route = userRouteList.get(0);
                route.setUserMoveImage(resultUrl);

                return ResponseEntity.ok(responseService.getSingleResult(true, "프로젝트 중단 성공", 200));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 중단 실패: 유저 경로 조회 실패"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 중단 실패: s3 이미지 저장 실패"));
    }

    @Transactional
    public ResponseEntity<?> stopRoute(ProjectStopRouteRequest request) {
        log.info("project stop route");
        log.info("request: " + request.toString());

        Optional<User> userOptional = userRepository.findById(request.getUserId());

        //유저 조회
        if(userOptional.isPresent()) {
            log.info("유저 조회");
            User user = userOptional.get();
            Optional<Project> projectOptional = projectRepository.findById(request.getProjectId());

            //프로젝트 조회
            if(projectOptional.isPresent()) {
                log.info("프로젝트 조회");
                Project project = projectOptional.get();

                //프로젝트가 종료되지 않았고, 누군가 참여중인지 확인
                if(!project.isProjectIsDone() && project.isProjectIsPlogging()) {
                    log.info("프로젝트 플로깅 중 확인");


                    //가장 가까운 도로 id 찾아서 map 에 넣기 (tmap api 호출) wait
                    HashMap<String, String> roadMap = performAsyncTasks(request.getUserMovePath());
                    log.info("가까운 도로 id 찾기");

                    //map 의 key 를 redis 에 저장 (road_{tmapid})
                    addRoadIntoRedis(roadMap);
                    log.info("redis 도로 저장");

                    //도로를 mongoDB 에 저장 (키 반환)
                    UserRouteDetail userRouteDetail = userRouteDetailRepository.save(UserRouteDetail.createUserRouteDetail(request));
                    log.info("mongoDB 도로 저장");

                    // UserRoute 객체 생성
                    UserRoute userRoute = UserRoute.builder()
                            .userId(request.getUserId())
                            .projectId(request.getProjectId())
                            .userNickname(request.getUserNickname())
                            .projectName(request.getProjectName())
                            .userMoveStart(request.getMoveStart())
                            .userMoveEnd(request.getMoveEnd())
                            .userMoveDistance(request.getMoveDistance())
                            .userMoveTime(request.getMoveTime())
                            .userMovePath(userRouteDetail.getUserRouteDetailId())
                            .userMoveMemo(request.getMoveMemo())
                            .build();

                    // UserRoute 객체 저장
                    userRouteRepository.save(userRoute);
                    log.info("UserRoute 저장");

                    // 프로젝트 수정
                    org.springframework.data.geo.Point lastPoint = request.getUserMovePath().get(request.getUserMovePath().size() - 1);
                    GeometryFactory geometryFactory = new GeometryFactory();
                    Coordinate coordinate = new Coordinate(lastPoint.getX(), lastPoint.getY()); // x 좌표와 y 좌표
                    Point point = geometryFactory.createPoint(coordinate);

                    project.setProjectStopCoordinate(point);    //중단점 좌표 등록
                    project.setProjectIsPlogging(false);        //플로깅 중인지 여부 = false
                    project.setProjectRemainingDistance(project.getProjectRemainingDistance() - request.getMoveDistance()); //현재 남은 프로젝트 거리
                    project.setProjectTotalContributer(project.getProjectTotalContributer() + 1);       // 프로젝트 참여 유저 수
                    project.setProjectCoordinateCurrentSize(request.getProjectCoordinateCurrentSize()); // 프로젝트 진행율을 위한 진행된 경로 정점 개수
                    log.info("프로젝트 수정");

                    // 랭킹 업데이트
                    rankingService.addOrUpdateRanking(user.getUserNickname(), request.getMoveDistance());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    // 코인 지급
                    payCoin(request, user, 10, "프로젝트 참여 포인트 지급");

                    // 프로젝트 완료
                    if(project.isProjectIsPath()) {
                        log.info("current: " + project.getProjectCoordinateCurrentSize() + ", total: " + project.getProjectCoordinateTotalSize());
                        if(project.getProjectCoordinateCurrentSize() >= project.getProjectCoordinateTotalSize()) {
                            log.info("경로 기반 프로젝트 완료");
                            project.setProjectEndDate(simpleDateFormat.format(new Date()));
                            project.setProjectIsDone(true);
                            sendFCM(project.getProjectId());    //FCM
                            payCoin(request, user, 20, "프로젝트 완료 포인트 지급"); //코인 지급
                        }
                    } else {
                        if(project.getProjectRemainingDistance() <= 0) {
                            log.info("거리 기반 프로젝트 완료");
                            project.setProjectEndDate(simpleDateFormat.format(new Date()));
                            project.setProjectIsDone(true);
                            sendFCM(project.getProjectId());    //FCM
                            payCoin(request, user, 20, "프로젝트 완료 포인트 지급"); //코인 지급
                        }
                    }
                    return ResponseEntity.ok(responseService.getSingleResult(true, "프로젝트 중단 성공", 200));
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 중단 실패: 프로젝트가 이미 종료되거나, 플로깅 중이지 않음"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 중단 실패: 프로젝트가 존재하지 않음"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 중단 실패: 유저가 존재하지 않음"));
    }

    private void payCoin(ProjectStopRouteRequest request, User user, int standard, String eventDetail) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Coin coin = Coin.builder()
                .coinEventDate(simpleDateFormat.format(new Date()))
                .coinAmount(request.getMoveDistance() / standard)
                .coinEventDetail(eventDetail)
                .user(user)
                .build();

        coinRepository.save(coin);
    }

    private void sendFCM(Long projectId) {
        // 해당 플로깅 참여한 유저 조회
        List<UserRoute> userIdList = userRouteRepository.findDistinctUserIdByProjectId(projectId);

        // 토큰 수집
        fcmTokenService.clearTokens();
        for(UserRoute userRoute : userIdList) {
            //유저 토큰 조회
            Optional<FcmToken> selectedTokens = fcmTokenRepository.findByUserId(userRoute.getUserId());
            if(selectedTokens.isPresent()) {
                fcmTokenService.addTokens(selectedTokens.get().getFcmToken());
            } else {
                log.info("해당 유저의 fcm token 이 존재하지 않습니다.");
            }
        }

        // 메세지 전송
        try {
            fcmTokenService.broadCastDataMessage("참여하신 플로깅이 완료되었습니다.", "projectId: " + projectId);
        } catch (Exception e) {
            log.info("FCM 메시지 전송 실패");
        }
    }


    private void addRoadIntoRedis(HashMap<String, String> roadMap) {
        log.info("Redis linkId 저장");

        //플로깅 도로 만료시간
        Long roadExpirationTime = 1000 * 60 * 60 * 24L;

        // Redis에 한 번에 여러 개의 키-값 쌍 저장
        redisTemplate.opsForValue().multiSet(roadMap);

        // 만료시간 설정
        for (String linkId : roadMap.keySet()) {
            redisTemplate.expire(linkId, roadExpirationTime, TimeUnit.MILLISECONDS);
        }
    }

    // 비동기 메서드 정의
    public CompletableFuture<TmapApiResponse> asyncMethod(org.springframework.data.geo.Point point) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 5초 후에 작업 수행
                Thread.sleep(100);
                log.info("Tmap api 호출");

                // tmap api 가까운 도로 호출
                TmapApiResponse response = tmapService.callTmapApi(point.getX(), point.getY());
                log.info("TmapApiResponse: " + response.toString());

                return response;
            } catch (InterruptedException e) {
                // 예외 처리
                Thread.currentThread().interrupt();
                return null;
            }
        });
    }

    // 비동기 작업들을 수행하고 모든 작업이 완료될 때까지 기다리는 메서드
    public HashMap<String, String> performAsyncTasks(List<org.springframework.data.geo.Point> coordinates) {
        log.info("Tmap api 호출");

        List<CompletableFuture<TmapApiResponse>> futures = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();


        // 비동기 메서드 호출 및 CompletableFuture 객체 저장 (마지막 플로깅 장소 제외)
        for (int i = 0; i < coordinates.size() - 1; i++) {
            futures.add(asyncMethod(coordinates.get(i)));
        }

        // 모든 CompletableFuture 객체를 조합하여 하나의 CompletableFuture 생성
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        // 모든 작업이 완료될 때까지 기다림
        allOf.join();

        // 결과를 처리
        for (CompletableFuture<TmapApiResponse> future : futures) {
            try {
                TmapApiResponse response = future.get(); // 결과를 얻음
                if (response != null && response.getResultData().getHeader() != null) {
                    Long linkId = response.getResultData().getHeader().getLinkId();
                    map.put("road_" + linkId, "This road has already been plugged.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.info("");
            }
        }

        // 결과 반환
        return map;
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

    // 안하기로 함.
    // recommendLineString 설정 (MongoDB에서 가져오는 로직이므로 가정하여 작성)

}
