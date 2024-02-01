package com.ssafy.relpl.service;

import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.request.UserAutoLoginRequest;
import com.ssafy.relpl.dto.request.UserLoginRequest;
import com.ssafy.relpl.dto.request.UserReissueRequest;
import com.ssafy.relpl.dto.request.UserSignupRequest;
import com.ssafy.relpl.dto.response.UserLoginResponse;
import com.ssafy.relpl.dto.response.UserReissueResponse;
import com.ssafy.relpl.dto.response.UserSignupResponse;
import com.ssafy.relpl.service.result.CommonResult;
import com.ssafy.relpl.util.jwt.ExceptionResponseHandler;
import com.ssafy.relpl.util.jwt.JwtTokenProvider;
import com.ssafy.relpl.util.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResponseService responseService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;
    @Autowired
    private AuthenticationManager authenticationManager;

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

                    return ResponseEntity.ok(responseService.getSingleResult(UserReissueResponse.createUserReissueResponse(accessToken, refreshToken), "재발급 성공", 200));
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.getFailResult(401, "refresh token 이 일치하지 않습니다."));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.getFailResult(401, "재발급 실패"));
        } catch (BaseException e) {
            return ResponseEntity.status(HttpStatus.GONE).body(responseService.getFailResult(410, "만료된 토큰입니다. 다시 로그인하세요."));
        } catch (Exception e) {
            log.info("error 5 : " + e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseService.getFailResult(401, "재발급 실패"));
        }
    }

    public ResponseEntity<String> test() {

        return ResponseEntity.ok("Success");
    }

}
