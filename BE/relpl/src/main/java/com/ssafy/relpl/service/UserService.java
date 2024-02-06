package com.ssafy.relpl.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssafy.relpl.config.AWSS3Config;
import com.ssafy.relpl.db.postgre.entity.Project;
import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.entity.UserRoute;
import com.ssafy.relpl.db.postgre.repository.ProjectRepository;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.db.postgre.repository.UserRouteRepository;
import com.ssafy.relpl.dto.request.*;
import com.ssafy.relpl.dto.response.*;
import com.ssafy.relpl.service.result.CommonResult;
import com.ssafy.relpl.util.common.UserHistoryDetailEntry;
import com.ssafy.relpl.util.jwt.JwtTokenProvider;
import com.ssafy.relpl.util.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRouteRepository userRouteRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
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
        if (find.isPresent()){
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
    };

    public ResponseEntity<CommonResult> login(UserLoginRequest request) {
        log.info("start");
        Optional<User> user = userRepository.findByUserUid(request.getUserUid());

        //유저 아이디가 존재하고, 유저 아이디와 비밀번호가 일치하는 경우
        if(user.isPresent() && passwordEncoder.matches(request.getUserPassword(), user.get().getUserPassword())) {
            //totalCoin 조회 필요
            //totalDistance 조회 필요
            //totalReport 조회 필요
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(String.valueOf(user.get().getUserId()), request.getUserPassword())
            );
            log.info("Userservice login authentication: " + authentication);

            //토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(jwtTokenProvider, user.get().getUserId());
            String refreshToken = jwtTokenProvider.createRefreshToken(jwtTokenProvider, authentication.getName());

            return ResponseEntity.ok(responseService.getSingleResult(UserLoginResponse.createUserLoginResponse(user.get(), accessToken, refreshToken), "로그인 성공", 200));
        }

        //유저 아이디가 존재하지 않거나, 비밀번호가 일치하지 않는 경우
        return ResponseEntity.badRequest().body(responseService.getFailResult(400, "로그인 실패"));
    }

    public ResponseEntity<CommonResult> autologin(UserAutoLoginRequest request) {
        Optional<User> user = userRepository.findById(request.getUserId());
        if(user.isPresent()) {
            if(user.get().isUserIsActive()) {
                //totalCoin 조회 필요
                //totalDistance 조회 필요
                //totalReport 조회 필요

                //유저가 존재하고, 회원탈퇴를 하지 않은 경우
                return ResponseEntity.ok(responseService.getSingleResult(UserLoginResponse.createUserLoginResponse(user.get(), "accessToken", "refreshToken"), "로그인 성공", 200));
            }
            log.info("isUserIsActive: " + user.get().isUserIsActive());
            //유저가 존재하고, 회원탈퇴를 한 경우
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.getFailResult(401, "로그인 실패"));
        }
        log.info("isPresent: " + user.isPresent());
        //유저가 존재하지 않은 경우
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.getFailResult(401, "로그인 실패"));
    }

    public ResponseEntity<CommonResult> reissue(UserReissueRequest request) {
        Optional<User> user = userRepository.findById(request.getUserId());
        if(!(user.isPresent() && user.get().isUserIsActive())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.getFailResult(401, "존재하지 않는 유저입니다."));
        }
        try {
            // JWT 토큰이 존재하고 유효한 경우
            if (!request.getRefreshToken().isEmpty() && jwtTokenProvider.validateToken(request.getRefreshToken())) {
                //refreshToken 의 name 으로 조회했을 때 존재하고, refreshToken 과 일치하는지 확인
                String savedToken = (String) redisTemplate.opsForValue().get("token_" + request.getUserId());
                log.info("saved refreshToken: " + savedToken);
                if(request.getRefreshToken().equals(savedToken)) {
                    //재발급
                    String accessToken = jwtTokenProvider.createAccessToken(jwtTokenProvider, request.getUserId());
                    String refreshToken = jwtTokenProvider.createRefreshToken(jwtTokenProvider, String.valueOf(request.getUserId()));

                    log.info("reissue success");
                    return ResponseEntity.ok(responseService.getSingleResult(UserReissueResponse.createUserReissueResponse(accessToken, refreshToken), "재발급 성공", 200));
                }
                log.info("reissue 401 error : refresh token 이 일치하지 않습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.getFailResult(401, "refresh token 이 일치하지 않습니다."));
            }
            log.info("reissue 401 error : 재발급 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.getFailResult(401, "재발급 실패"));
        } catch (BaseException e) {
            log.info("reissue 401 error : 만료된 토큰입니다. 다시 로그인하세요.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.getFailResult(401, "만료된 토큰입니다. 다시 로그인하세요."));
        } catch (Exception e) {
            log.info("reissue 401 error : 재발급 실패");
            log.info("error 5 : " + e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.getFailResult(401, "재발급 실패"));
        }
    }

    public ResponseEntity<CommonResult> duplicateNickname(String nickname) {
        if(userRepository.findByUserNickname(nickname).isPresent()) {
            return ResponseEntity.ok(responseService.getSingleResult(UserDuplicateNicknameResponse.createUserDuplicateNicknameResponse(true), "닉네임 사용 불가능", 200));
        }
        return ResponseEntity.ok(responseService.getSingleResult(UserDuplicateNicknameResponse.createUserDuplicateNicknameResponse(false), "닉네임 사용 가능", 200));
    }

    public ResponseEntity<CommonResult> duplicateUserPhone(UserDuplicatePhoneRequest request) {
        if(userRepository.findByUserPhone(request.getUserPhone()).isPresent()) {
            return ResponseEntity.ok(responseService.getSingleResult(UserDuplicatePhoneResponse.createUserDuplicatePhoneResponse(true), "휴대폰번호 사용 불가능", 200));
        }
        return ResponseEntity.ok(responseService.getSingleResult(UserDuplicatePhoneResponse.createUserDuplicatePhoneResponse(false), "휴대폰번호 사용 가능", 200));
    }

    public ResponseEntity<CommonResult> duplicateUserId(UserDuplicateIdRequest request) {
        if(userRepository.findByUserUid(request.getUserUid()).isPresent()){
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

        if(userOptional.isPresent()) {
            User user = userOptional.get();

            //totalCoin 조회 필요
            //totalDistance 조회 필요
            //totalReport 조회 필요

            //유저가 존재하고, 회원탈퇴를 하지 않은 경우
            return ResponseEntity.ok(responseService.getSingleResult(UserLoginResponse.createUserLoginResponse(user, "accessToken", "refreshToken"), "로그인 성공", 200));
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

                    log.info("저장된 프로필 사진 url:{}",user.getUserImage());
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



    public ResponseEntity<CommonResult> getUserHistoryDetail(UserHistoryDetailRequest request) {

        log.info("getUserHistoryDetail 내부로 들어옴");

        // 릴레이 아이디로 릴레이 있는지 조회
        Optional<Project> projectOptional = projectRepository.findById(request.getProjectId());

        // 릴레이 아이디로 userRoute 테이블에서 참여한 유저 정보 조회
        List<UserRoute> userRouteList = userRouteRepository.findByProjectId(request.getProjectId());

        // 유저별 릴레이 정보 리스트
        ArrayList<UserHistoryDetailEntry> detailList = new ArrayList<>();


        
        // 로직2
        // projectTime : project.EndDate - project.CreateDate
        // 5일 18시간 38분 동안
        
        // 로직3
        // projectPeople : 한 명의 유저가 같은 릴레이에 여러 번 참여한 경우 중복 제거
        // 네 분께서
        
        // 존재하는 프로젝트일 경우
        if (projectOptional.isPresent()) {

            // 릴레이 1개 객체
            Project project = projectOptional.get();

            for (UserRoute userRoute : userRouteList) {

                // 로직1
                // moveContribution : 전체 프로젝트 거리 대비 해당 유저의 moveDistance %
                // 릴레이 기여도 64%
                int moveContribution = (userRoute.getUserMoveDistance() / project.getProjectTotalDistance()) * 100;

                UserHistoryDetailEntry entry = UserHistoryDetailEntry.builder()
                        .userNickname(userRoute.getUserNickname())
                        .moveStart(userRoute.getUserMoveStart())
                        .moveEnd(userRoute.getUserMoveEnd())
                        .moveDistance(userRoute.getUserMoveDistance())
                        .moveTime(String.valueOf(userRoute.getUserMoveTime()))
                        .moveMemo(userRoute.getUserMoveMemo())
                        .moveContribution(moveContribution)
                        .moveImage(userRoute.getUserMoveImage())
                        .build();


                detailList.add(entry);
            }


            // 날짜 포맷 정의
            java.time.format.DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // 문자열을 LocalDateTime 객체로 파싱
            LocalDateTime createDateTime = LocalDateTime.parse(project.getProjectCreateDate(), formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(project.getProjectEndDate(), formatter);

            // createDate와 endDate 사이의 분 차이를 계산
            long projectTime = ChronoUnit.MINUTES.between(createDateTime, endDateTime);


            UserHistoryDetailResponse response = UserHistoryDetailResponse.builder()
                    .projectName(project.getProjectName())
                    .projectDistance(project.getProjectTotalDistance())
                    .projectTime()
                    .projectPeople(project.getProjectTotalContributer())
                    .detailList(detailList)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(responseService.getSingleResult(response, "프로젝트 상세내역 조회에 성공하였습니다.", 200));
        } else { // 존재하지 않는 프로젝트일 경우
            log.error("projectId 가 없음");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "프로젝트 상세내역 조회에 실패하였습니다."));
        }

    }


}
