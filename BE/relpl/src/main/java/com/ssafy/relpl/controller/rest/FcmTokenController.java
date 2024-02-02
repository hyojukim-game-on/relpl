package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.dto.request.FcmTokenRequest;
import com.ssafy.relpl.service.FcmTokenService;
import com.ssafy.relpl.service.ResponseService;
import com.ssafy.relpl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/push/token")
@RequiredArgsConstructor
public class FcmTokenController {

    private final UserService userService;
    private final ResponseService responseService;
    private final FcmTokenService fcmTokenService;

    //fcmtoken 등록 여부 확인 메서드
    @PostMapping
    public ResponseEntity<?> pushFcmToken(@RequestBody FcmTokenRequest request) {
        // FcmTokenService 에 존재하는 fcmtoken 등록 여부 로직 반환
        return fcmTokenService.checkFcmToken(request);
    }
}