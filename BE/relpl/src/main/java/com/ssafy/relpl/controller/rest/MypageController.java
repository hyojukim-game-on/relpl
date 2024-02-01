package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.dto.request.MypageChangePasswordRequest;
import com.ssafy.relpl.service.MypageService;
import com.ssafy.relpl.service.result.CommonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    @PutMapping("/password")
    public ResponseEntity<CommonResult> changePassword(@RequestBody MypageChangePasswordRequest request) {
        log.info("changePassword");
        return mypageService.changePassword(request);
    }


}
