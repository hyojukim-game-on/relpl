package com.ssafy.relpl.service;

import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.request.MypageChangePasswordRequest;
import com.ssafy.relpl.dto.request.UserLoginRequest;
import com.ssafy.relpl.dto.response.UserLoginResponse;
import com.ssafy.relpl.service.result.CommonResult;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResponseService responseService;

    
    /* changePassword : 기존 비밀번호와 일치하면 새로운 비밀번호로 변경
    @param : 비밀번호 변경 시 입력받는 정보 DTO
    @return : 200 성공 or 400 실패
    * */
    public ResponseEntity<CommonResult> changePassword(MypageChangePasswordRequest request) {
        log.info("비밀번호 변경 시작");
        Optional<User> userOptional = userRepository.findById(request.getUserId());

        // 기존 유저가 존재함
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 기존 비밀번호가 일치하는 경우
            if (passwordEncoder.matches(request.getCurrentPassword(), user.getUserPassword())) {
                // 새 비밀번호로 업데이트
                user.setUserPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);  // 변경사항 저장
                return ResponseEntity.ok(responseService.getSingleResult(true, "비밀번호 변경 성공", 200));
            } else {
                // 현재 비밀번호 불일치
                return ResponseEntity.badRequest().body(responseService.getFailResult(400,  "비밀번호 일치하지 않음"));
            }
        }   // 유저가 존재하지 않음
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "존재하지 않는 유저"));
        }

    }