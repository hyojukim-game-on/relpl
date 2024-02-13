package com.ssafy.relpl.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssafy.relpl.config.AWSS3Config;
import com.ssafy.relpl.db.mongo.entity.UserRouteDetail;
import com.ssafy.relpl.db.mongo.repository.UserRouteDetailRepository;
import com.ssafy.relpl.db.postgre.entity.*;
import com.ssafy.relpl.db.postgre.entity.Project;
import com.ssafy.relpl.db.postgre.repository.*;
import com.ssafy.relpl.dto.request.*;
import com.ssafy.relpl.dto.response.*;
import com.ssafy.relpl.service.result.CommonResult;
import com.ssafy.relpl.util.common.UserHistoryDetailEntry;
import com.ssafy.relpl.util.jwt.JwtTokenProvider;
import com.ssafy.relpl.util.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.lang.reflect.Array;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final UserRouteRepository userRouteRepository;
    private final UserRouteDetailRepository userRouteDetailRepository;
    private final ProjectRepository projectRepository;
    private final CoinRepository coinRepository;
    private final ReportRepository reportRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResponseService responseService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    //    private final RedisTemplate redisTemplate;
    private final AuthenticationManager authenticationManager;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.base-url}")
    private String baseUrl;

    private final AWSS3Config awss3Config;
    private final AmazonS3Client amazonS3Client;


    public ResponseEntity<CommonResult> save(UserSignupRequest request) throws BaseException {
        //사용자가 이미 존재하는지 확인
        Optional<User> find = userRepository.findByUserUid(request.getUserUid());
        if (find.isPresent()) {
            //사용자가 이미 있다면 Failed 반환
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "회원가입 실패"));
        }
        //회원가입 진행 (사용자가 없을 경우)
        User saved = userRepository.save(User.builder()
                .userUid(request.getUserUid())
                .userNickname(request.getUserNickname())
                .userPassword(passwordEncoder.encode(request.getUserPassword()))
                .userPhone(request.getUserPhone())
                .userIsActive(true)
                .build());
        //회원가입 성공 응답 반환
        return ResponseEntity.ok(responseService.getSingleResult(UserSignupResponse.createUserSignupResponse(saved), "회원가입 성공", 200));
    }

    public ResponseEntity<CommonResult> login(UserLoginRequest request) {
        log.info("user login");
        Optional<User> userOptional = userRepository.findByUserUid(request.getUserUid());

        //유저 아이디가 존재하고, 유저 아이디와 비밀번호가 일치하는 경우
        if (userOptional.isPresent() && passwordEncoder.matches(request.getUserPassword(), userOptional.get().getUserPassword())) {
            User user = userOptional.get();

            //총 포인트, 총 이동거리, 총 제보횟수 집계
            int totalCoin = coinRepository.sumCoinAmountByUserId(user.getUserId());
            int totalDistance = userRouteRepository.sumUserMoveDistanceByUserId(user.getUserId());
            int totalReport = reportRepository.countByUser(user);
            log.info("totalCoin: " + totalCoin + ", totalDistance: " + totalDistance + ", totalReport: " + totalReport);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(String.valueOf(user.getUserId()), request.getUserPassword())
            );
            log.info("Userservice login authentication: " + authentication);

            //토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(jwtTokenProvider, user.getUserId());
            String refreshToken = jwtTokenProvider.createRefreshToken(jwtTokenProvider, authentication.getName());

            return ResponseEntity.ok(responseService.getSingleResult(UserLoginResponse.createUserLoginResponse(user, accessToken, refreshToken, totalCoin, totalDistance, totalReport), "로그인 성공", 200));
        }

        //유저 아이디가 존재하지 않거나, 비밀번호가 일치하지 않는 경우
        return ResponseEntity.badRequest().body(responseService.getFailResult(400, "로그인 실패"));
    }

    public ResponseEntity<CommonResult> autologin(UserAutoLoginRequest request) {
        log.info("user autologin");
        Optional<User> userOptional = userRepository.findById(request.getUserId());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.isUserIsActive()) {
                //총 포인트, 총 이동거리, 총 제보횟수 집계
                int totalCoin = coinRepository.sumCoinAmountByUserId(user.getUserId());
                int totalDistance = userRouteRepository.sumUserMoveDistanceByUserId(user.getUserId());
                int totalReport = reportRepository.countByUser(user);
                log.info("totalCoin: " + totalCoin + ", totalDistance: " + totalDistance + ", totalReport: " + totalReport);

                //유저가 존재하고, 회원탈퇴를 하지 않은 경우
                return ResponseEntity.ok(responseService.getSingleResult(UserLoginResponse.createUserLoginResponse(user, "accessToken", "refreshToken", totalCoin, totalDistance, totalReport), "로그인 성공", 200));
            }

            //유저가 존재하고, 회원탈퇴를 한 경우
            log.info("isUserIsActive: " + user.isUserIsActive());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.getFailResult(401, "로그인 실패"));
        }

        //유저가 존재하지 않은 경우
        log.info("isPresent: " + userOptional.isPresent());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.getFailResult(401, "로그인 실패"));
    }


    public ResponseEntity<CommonResult> reissue(UserReissueRequest request) {
        log.info("user reissue");
        Optional<User> user = userRepository.findById(request.getUserId());

        if (!(user.isPresent() && user.get().isUserIsActive())) {
            return unauthorizedResponse("존재하지 않는 유저입니다.");
        }

        try {
            if (request.getRefreshToken().isEmpty() || !jwtTokenProvider.validateToken(request.getRefreshToken())) {
                return unauthorizedResponse("refresh token 이 일치하지 않거나 존재하지 않습니다.");
            }

            String savedToken = (String) redisTemplate.opsForValue().get("token_" + request.getUserId());
            if (!request.getRefreshToken().equals(savedToken)) {
                return unauthorizedResponse("refresh token 이 일치하지 않습니다.");
            }

            String accessToken = jwtTokenProvider.createAccessToken(jwtTokenProvider, request.getUserId());
            String refreshToken = jwtTokenProvider.createRefreshToken(jwtTokenProvider, String.valueOf(request.getUserId()));

            log.info("토큰 재발급 성공");
            return ResponseEntity.ok(responseService.getSingleResult(UserReissueResponse.createUserReissueResponse(accessToken, refreshToken), "재발급 성공", HttpStatus.OK.value()));
        } catch (BaseException e) {
            log.info("만료된 토큰입니다. 다시 로그인하세요.");
            return unauthorizedResponse("만료된 토큰입니다. 다시 로그인하세요.");
        } catch (Exception e) {
            log.error("재발급 실패", e);
            return unauthorizedResponse("재발급 실패");
        }
    }

    private ResponseEntity<CommonResult> unauthorizedResponse(String message) {
        log.info("401 Unauthorized 오류: {}", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.getFailResult(HttpStatus.UNAUTHORIZED.value(), message));
    }

    public ResponseEntity<CommonResult> duplicateNickname(String nickname) {
        if (userRepository.findByUserNickname(nickname).isPresent()) {
            return ResponseEntity.ok(responseService.getSingleResult(UserDuplicateNicknameResponse.createUserDuplicateNicknameResponse(true), "닉네임 사용 불가능", 200));
        }
        return ResponseEntity.ok(responseService.getSingleResult(UserDuplicateNicknameResponse.createUserDuplicateNicknameResponse(false), "닉네임 사용 가능", 200));
    }

    public ResponseEntity<CommonResult> duplicateUserPhone(UserDuplicatePhoneRequest request) {
        if (userRepository.findByUserPhone(request.getUserPhone()).isPresent()) {
            return ResponseEntity.ok(responseService.getSingleResult(UserDuplicatePhoneResponse.createUserDuplicatePhoneResponse(true), "휴대폰번호 사용 불가능", 200));
        }
        return ResponseEntity.ok(responseService.getSingleResult(UserDuplicatePhoneResponse.createUserDuplicatePhoneResponse(false), "휴대폰번호 사용 가능", 200));
    }

    public ResponseEntity<CommonResult> duplicateUserId(UserDuplicateIdRequest request) {
        if (userRepository.findByUserUid(request.getUserUid()).isPresent()) {
            //중복된 ID 있음
            return ResponseEntity.ok(responseService.getSingleResult(UserDuplicateIdResponse.createUserDuplicateIdResponse(true), "아이디 사용 불가능", 200));
        }
        //중복된 ID 없음
        return ResponseEntity.ok(responseService.getSingleResult(UserDuplicateIdResponse.createUserDuplicateIdResponse(false), "아이디 사용 가능", 200));
    }


    public ResponseEntity<String> test() {

        return ResponseEntity.ok("Success");
    }

    public ResponseEntity<?> getUserInfo(UserInfoRequest request) {
        Optional<User> userOptional = userRepository.findById(request.getUserId());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            //총 포인트, 총 이동거리, 총 제보횟수 집계
            int totalCoin = coinRepository.sumCoinAmountByUserId(user.getUserId());
            int totalDistance = userRouteRepository.sumUserMoveDistanceByUserId(user.getUserId());
            int totalReport = reportRepository.countByUser(user);
            log.info("totalCoin: " + totalCoin + ", totalDistance: " + totalDistance + ", totalReport: " + totalReport);

            //유저가 존재하고, 회원탈퇴를 하지 않은 경우
            return ResponseEntity.ok(responseService.getSingleResult(UserLoginResponse.createUserLoginResponse(user, "accessToken", "refreshToken", totalCoin, totalDistance, totalReport), "로그인 성공", 200));
        }
        // 기존 유저가 없음
        log.info("유저 조회 실패");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "유저 정보 조회 실패"));
    }

    /* setProfilePic : 유저가 제공한 사진 파일로 유저 프로필 사진 설정
    @param : 유저가 제공한 사진 파일
    @return : 성공 시) uploadedFileUrl s3에 업로드 한 프로필 사진 url , 실패 시) null
    * */
    public ResponseEntity<CommonResult> setProfilePic(UserProfileRequest request) throws IOException {

        log.info("프로필 사진 설정");
        Optional<User> userOptional = userRepository.findById(request.getUserId());

        // 가입한 유저가 존재함
        if (userOptional.isPresent()) {

            log.info("가입한 유저가 존재함");

            // 가입한 유저 가져오기
            User user = userOptional.get();

            // 프로필 사진 설정
            log.info("프로필 사진 url(처음이므로 null 나와야 함) : {}", user.getUserImage());

            // 이미지를 s3에 올리고 받은 url
            String resultUrl = setUserProfilePhoto(request.getFile());

            if (resultUrl != null) {

                log.info("프로필 사진 s3에 업로드 성공");

                // s3에 올린 파일 주소를 db에 유저 객체로 저장하기
                user.setUserImage(resultUrl);
                userRepository.save(user);

                log.info("저장된 프로필 사진 url:{}", user.getUserImage());
                log.info(resultUrl);
                log.info("DB에 프로필 사진 업로드 성공");

                // 업로드가 성공했을 경우 성공 응답 반환
                return ResponseEntity.status(HttpStatus.OK).body(responseService.getSingleResult(true, "OK", 200));
            }
            // 업로드가 실패했을 경우 실패 응답 반환
            else {
                log.info("프로필 사진 s3에 업로드 실패");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "Bad Request"));
            }
        }
        // 기존 유저가 없음
        log.info("기존 유저가 없음");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "Bad Request"));
    }


    /* setUserProfilePhoto : s3에 유저 프로필 사진 업로드하기
    @param : 유저가 제공한 사진 파일
    @return : 성공 시) uploadedFileUrl s3에 업로드 한 프로필 사진 url , 실패 시) null
    * */
    public String setUserProfilePhoto(MultipartFile file) {
        try {

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


    /* getUserHistory : 내 기록 조회 (유저가 참여한 릴레이 조회)
     * @param : UserHistoryRequest (= userId)
     * @return : UserHistoryResponse (= 참여 릴레이 수, 누적 거리, 누적 시간, 릴레이 별 상세 정보 리스트)
     * */
    public ResponseEntity<CommonResult> getUserHistory(UserHistoryRequest request) {
        log.info("UserService 내의 getUserHistory 로 들어옴");

        try {
            // UserRoute 테치블에서 userId 레코드 가져오기
            List<UserRoute> userRouteList = userRouteRepository.findByUserId(request.getUserId());
            log.info("userRouteList:{}", userRouteList);

            // 초기화
            int totalProjects = userRouteList.size();
            int userTotalDistance = 0;
            int userTotalTime = 0;
            List<Map<String, Object>> detailList = new ArrayList<>();

            if (totalProjects != 0) {
                userTotalDistance = userRouteRepository.sumUserMoveDistanceByUserId(request.getUserId());
                userTotalTime = userRouteRepository.sumUserMoveTimeByUserId(request.getUserId());

                Set<Long> projectIds = userRouteList.stream().map(UserRoute::getProjectId).collect(Collectors.toSet());
                Map<Long, Project> projects = projectRepository.findAllById(projectIds).stream()
                        .collect(Collectors.toMap(Project::getProjectId, project -> project));

                detailList = userRouteList.stream().map(userRoute -> {
                    Project project = projects.get(userRoute.getProjectId());
                    Map<String, Object> projectDetails = new HashMap<>();
                    if (project != null) {
                        projectDetails.put("projectId", project.getProjectId());
                        projectDetails.put("projectName", project.getProjectName());
                        projectDetails.put("projectIsDone", project.isProjectIsDone());
                        projectDetails.put("createDate", project.getProjectCreateDate());
                        projectDetails.put("endDate", project.getProjectEndDate());
                        projectDetails.put("totalDistance", project.getProjectTotalDistance());
                        projectDetails.put("totalContributor", project.getProjectTotalContributer());
                    }
                    return projectDetails;
                }).collect(Collectors.toList());
            }

            UserHistoryResponse response = UserHistoryResponse.builder()
                    .totalProject(totalProjects)
                    .userTotalDistance(userTotalDistance)
                    .userTotalTime(userTotalTime)
                    .detailList((ArrayList<Map<String, Object>>) detailList)
                    .build();

            return ResponseEntity.ok(responseService.getSingleResult(response, "OK", 200));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "BAD REQUEST"));
        }
    }


    /* getUserHistoryDetail : projectId 로 [PostGreSQL] Project 테이블 조회
     * @param : projectId
     * @return : projectRepository 조회 후 해당 프로젝트 없을 경우 에러 반환
     * */
    public ResponseEntity<CommonResult> getUserHistoryDetail(UserHistoryDetailRequest request) {
        log.info("getUserHistoryDetail 내부로 들어옴");

        return projectRepository.findById(request.getProjectId())
                .map(project -> processProjectDetails(project, request))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 상세내역 조회에 실패하였습니다.")));
    }

    /* processProjectDetails : projectId 로 [PostGreSQL] UserRoute 테이블 조회
     * @param : projectId
     * @return : userRouteRepository 조회 후 해당 프로젝트 없을 경우 에러 반환
     * */
    private ResponseEntity<CommonResult> processProjectDetails(Project project, UserHistoryDetailRequest request) {
        List<UserRoute> userRouteList = userRouteRepository.findByProjectId(request.getProjectId());
        List<UserHistoryDetailEntry> detailList = userRouteList.stream()
                .map(userRoute -> createUserHistoryDetailEntry(userRoute, project))
                .collect(Collectors.toList());

        UserHistoryDetailResponse response = createUserHistoryDetailResponse(project, detailList);
        return ResponseEntity.status(HttpStatus.OK).body(responseService.getSingleResult(response, "프로젝트 상세내역 조회에 성공하였습니다.", 200));
    }

    private UserHistoryDetailEntry createUserHistoryDetailEntry(UserRoute userRoute, Project project) {
        Optional<UserRouteDetail> userRouteDetailList = userRouteDetailRepository.findById(userRoute.getUserMovePath());    //mongoDB
        List<Point> movePath = new ArrayList<>();

        if(userRouteDetailList.isPresent()) {
            movePath = GeoJsonLineStringToPoints(userRouteDetailList.get());
        }

        int moveContribution = calculateMoveContribution(userRoute.getUserMoveDistance(), project.getProjectTotalDistance());

        return UserHistoryDetailEntry.builder()
                .userNickname(userRoute.getUserNickname())
                .movePath(movePath)
                .moveStart(userRoute.getUserMoveStart())
                .moveEnd(userRoute.getUserMoveEnd())
                .moveDistance(userRoute.getUserMoveDistance())
                .moveTime(String.valueOf(userRoute.getUserMoveTime()))
                .moveMemo(userRoute.getUserMoveMemo())
                .moveContribution(moveContribution)
                .moveImage(userRoute.getUserMoveImage())
                .build();
    }

    private List<Point> GeoJsonLineStringToPoints(UserRouteDetail userRouteDetail) {
        List<Point> points = new ArrayList<>();
        for(Point point : userRouteDetail.getUserRouteCoordinate().getCoordinates()) {
            points.add(new Point(point.getX(), point.getY()));
        }
        return points;
    }

    private int calculateMoveContribution(double userMoveDistance, double projectTotalDistance) {
        return (int) ((userMoveDistance / projectTotalDistance) * 100);
    }

    private UserHistoryDetailResponse createUserHistoryDetailResponse(Project project, List<UserHistoryDetailEntry> detailList) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        LocalDateTime createDateTime = LocalDateTime.parse(project.getProjectCreateDate(), formatter);
//        LocalDateTime endDateTime = LocalDateTime.parse(project.getProjectEndDate(), formatter);
//        long projectTime = ChronoUnit.MINUTES.between(createDateTime, endDateTime);
        int timeSum = 0;
        for (UserHistoryDetailEntry entry : detailList) {
            timeSum += Integer.parseInt(entry.getMoveTime());
        }
        return UserHistoryDetailResponse.builder()
                .projectName(project.getProjectName())
                .projectDistance(project.getProjectTotalDistance())
                .projectTime(timeSum)
                .projectPeople(project.getProjectTotalContributer())
                .detailList(detailList)
                .build();
    }
}
