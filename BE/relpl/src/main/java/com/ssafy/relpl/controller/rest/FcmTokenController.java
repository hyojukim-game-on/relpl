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
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    //fcmtoken 등록
    @PostMapping("/push")
    public ResponseEntity<?> registFcmToken(@RequestBody FcmTokenRequest request) {
        return fcmTokenService.insert(request);
    }

}
