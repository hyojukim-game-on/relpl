package com.ssafy.relpl.service;

import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.request.UserLoginRequest;
import com.ssafy.relpl.dto.request.UserSignupRequest;
import com.ssafy.relpl.dto.response.UserLoginResponse;
import com.ssafy.relpl.dto.response.UserSignupResponse;
import com.ssafy.relpl.service.result.CommonResult;
import com.ssafy.relpl.service.result.SingleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<CommonResult> login(UserLoginRequest request) {
        //사용자가 존재하고,
        Optional<User> find = userRepository.findByUserUid(request.getUserUid());
        if (find.isPresent() && passwordEncoder.matches(request.getUserPassword(), find.get().getUserPassword())){
            //회원가입 성공 응답 반환
            SingleResult<UserLoginResponse> result = new SingleResult<>();
            result.setData(UserLoginResponse.createUserLoginResponse(find.get()));
            result.setCode(200);
            result.setMessage("로그인 성공");
            return ResponseEntity.ok(result);
        }
        //사용자가 존재하지 않거나, 비밀번호가 일치하지 않는다면
        CommonResult result = new CommonResult();
        result.setCode(400);
        result.setMessage("로그인 실패");
        return ResponseEntity.badRequest().body(result);
    };

    public ResponseEntity<CommonResult> save(UserSignupRequest request) {
        //사용자가 이미 존재하는지 확인
        Optional<User> find = userRepository.findByUserUid(request.getUserUid());
        if (find.isPresent()){
            //사용자가 이미 있다면 Failed 반환
            CommonResult result = new CommonResult();
            result.setCode(400);
            result.setMessage("Sign-up Failed");
            return ResponseEntity.badRequest().body(result);
        }
        //회원가입 진행 (사용자가 없을 경우)
        User saved = userRepository.save(User.builder()
                .userUid(request.getUserUid())
                .userNickname(request.getUserNickname())
                .userPassword(passwordEncoder.encode(request.getUserPassword()))
                .userPhone(request.getUserPhone())
                .build());
        //회원가입 성공 응답 반환
        SingleResult<UserSignupResponse> result = new SingleResult<>();
        result.setData(UserSignupResponse.createUserSignupResponse(saved));
        result.setCode(200);
        result.setMessage("Sign-up Success");
        return ResponseEntity.ok(result);
    };

    public User select(Long id) {
        Optional<User> finded = userRepository.findById(id);
        if(finded.isPresent()) {
            return finded.get();
        }
        return null;
    }
}
