package com.ssafy.relpl.service;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssafy.relpl.config.AWSS3Config;
import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.request.MypageChangePasswordRequest;
import com.ssafy.relpl.dto.request.MypageChangeRequest;
import com.ssafy.relpl.dto.request.MypageExitRequest;
import com.ssafy.relpl.service.result.CommonResult;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class MypageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.base-url}")
    private String baseUrl;

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResponseService responseService;
    private final AWSS3Config awss3Config;
    private final AmazonS3Client amazonS3Client;



    
    /* changePassword : 기존 비밀번호와 일치하면 새로운 비밀번호로 변경
    @param : 비밀번호 변경 시 입력받는 정보 DTO
    @return : 200 성공 or 400 실패
    * */
    public ResponseEntity<CommonResult> changePassword(MypageChangePasswordRequest request) {
        log.info("비밀번호 변경 시작");
        Optional<User> userOptional = userRepository.findById(request.getUserId());

        // 기존 유저가 존재함..
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 기존 비밀번호가 일치하는 경우..
            if (passwordEncoder.matches(request.getCurrentPassword(), user.getUserPassword())) {
                // 새 비밀번호로 업데이트..
                user.setUserPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);  // 변경사항 저장
                return ResponseEntity.ok(responseService.getSingleResult(true, "비밀번호 변경 성공", 200));
            } else {
                // 현재 비밀번호 불일치..
                return ResponseEntity.badRequest().body(responseService.getFailResult(400,  "비밀번호 일치하지 않음"));
            }
        }   // 유저가 존재하지 않음..
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "비밀번호 변경 실패"));
        }




        /* changeProfile : 새로운 프로필 정보로 변경 (닉네임, 핸드폰번호, 프로필사진)
        @param : 프로필 변경 시 입력받는 정보 DTO
        @return : 200 성공 or 400 실패
        * */
        public ResponseEntity<CommonResult> changeProfile(MypageChangeRequest request) throws IOException {

            log.info("프로필 변경");
            Optional<User> userOptional = userRepository.findById(request.getUserId());

            // 기존 유저가 존재함..
            if (userOptional.isPresent()) {
                
                log.info("기존 유저가 존재함");
                
                // 기존 유저 가져오기..
                User user = userOptional.get();

                // 기존 닉네임, 기존 핸드폰 번호..
                log.info("기존 닉네임:{}",user.getUserNickname());
                log.info("기존 핸드폰 번호:{}",user.getUserPhone());

                // 새로운 닉네임, 새로운 핸드폰 번호..
                user.setUserNickname(request.getUserNickname());
                user.setUserPhone(request.getUserPhone());

                // DB에 변경사항 저장..
                userRepository.save(user);  
                
                // 바뀐 닉네임, 바뀐 핸드폰 번호..
                log.info("바뀐 닉네임:{}",user.getUserNickname());
                log.info("바뀐 핸드폰 번호:{}",user.getUserPhone());
                
                // 유저가 프로필 사진을 제공한 경우..
                log.info("request.getUserProfilePhoto(): " + request.getUserProfilePhoto());
                if (request.getUserProfilePhoto() != null) {

                    log.info("유저가 프로필 사진을 제공함");
                    log.info(request.getUserProfilePhoto().getClass().getName());

                    // 프로필사진 s3에 업로드..
                    String resultUrl = updateUserProfilePhoto(request.getUserProfilePhoto());

                    // 업로드가 성공했을 경우 DB 에 새로 받은 url 값 업데이트..
                    if (resultUrl != null) {

                            log.info("프로필 사진 s3에 업로드 성공");
                            log.info("기존 유저 프로필 이미지:{}",user.getUserImage());

                            user.setUserImage(resultUrl);
                            userRepository.save(user);

                            log.info("새로운 프로필 이미지:{}",user.getUserImage());
                            log.info(resultUrl);
                            log.info("DB에 프로필 사진 업로드 성공");

                        return ResponseEntity.ok(responseService.getSingleResult(true, "정보 수정 성공", 200));
                    } // 업로드가 실패했을 경우 에러 반환..
                    else {
                        log.info("프로필 사진 s3에 업로드 실패");
                        return ResponseEntity.badRequest().body(responseService.getFailResult(400, "정보 수정 실패"));
                    }
                // 프로필 사진을 제공하지 않아서 기존 프로필 사진 유지, 그 외 정보만 변경..
                } else  {
                    log.info("프로필 사진 제공하지 않음");
                    return ResponseEntity.ok(responseService.getSingleResult(true, "정보 수정 성공", 200));
                }
            } // 기존 유저가 존재하지 않음..
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "정보 수정 실패"));
        }


        /* updateUserProfilePhoto : s3에 유저 프로필 사진 업로드하기
        @param : 유저가 제공한 사진 파일
        @return : 성공 시) uploadedFileUrl s3에 업로드 한 프로필 사진 url , 실패 시) null
        * */
        public String updateUserProfilePhoto(MultipartFile userProfilePhoto) {
            try{

                // 유저가 제공한 프로필 사진 파일의 원래 이름..
                String originalFileName = userProfilePhoto.getOriginalFilename();

                // 중복 방지를 위한 랜덤 값 String 을 앞에 추가..
                String s3UploadFileName = UUID.randomUUID() + originalFileName;

                // S3에 업로드 된 파일의 url 주소..
                String uploadedFileUrl = baseUrl + s3UploadFileName;

                // S3에 파일과 함께 올릴 메타데이터..
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(userProfilePhoto.getContentType());
                metadata.setContentLength(userProfilePhoto.getSize());

                // S3에 요청 보낼 객체 생성..
                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        bucket, s3UploadFileName, userProfilePhoto.getInputStream(), metadata
                );

                // S3에 요청 보내서 파일 업로드..
                putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
                amazonS3Client.putObject(putObjectRequest);

                log.info("s3에 프로필 사진 파일 업로드 성공");

                return uploadedFileUrl;

            } // S3에 파일 업로드 실패 시 에러 출력하고 null 리턴..
            catch (IOException e) {
                log.error(e.getMessage());
                return null;
            }
        }

        @Transactional
        public ResponseEntity<CommonResult> exit(MypageExitRequest request) {
            log.info("회원탈퇴");

            //유저 조회
            Optional<User> userOptional = userRepository.findByUserUid(request.getUserUid());

            //유저가 존재하는지 확인
            if(userOptional.isPresent()) {
                User user = userOptional.get();

                //유저의 패스워드 일치 확인
                if (passwordEncoder.matches(request.getUserPassword(), user.getUserPassword())) {

                    //유저 refresh token 조회
                    String refreshToken = (String) redisTemplate.opsForValue().get("token_" + user.getUserId());

                    //조회에서 존재하는 경우 삭제
                    if(refreshToken != null) {
                        redisTemplate.delete("token_" + user.getUserId());
                    }

                    //유저 비활성화
                    user.setUserIsActive(false);

                    //회원탈퇴 성공
                    return ResponseEntity.ok(responseService.getSingleResult(true, "회원탈퇴 성공", 200));
                }
                //회원탈퇴 실패(비밀번호 불일치)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "비밀번호 일치하지 않음"));
            }
            //회원탈퇴 실패(유저가 존재하지 않음)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "회원탈퇴 실패"));
        }

    }