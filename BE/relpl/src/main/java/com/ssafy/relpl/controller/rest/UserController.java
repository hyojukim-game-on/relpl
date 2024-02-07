package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.dto.request.*;
import com.ssafy.relpl.service.UserService;
import com.ssafy.relpl.service.result.CommonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResult> save(@RequestBody UserSignupRequest request) {
        return userService.save(request);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResult> userLogin(@RequestBody UserLoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/autologin")
    public ResponseEntity<CommonResult> autologin(@RequestBody UserAutoLoginRequest request) {
        return userService.autologin(request);
    }

    @PostMapping("/token/reissue")
    public ResponseEntity<CommonResult> reissue(@RequestBody UserReissueRequest request) {
        return userService.reissue(request);
    }

    @GetMapping("/isExist/nickname/{nickname}")
    public ResponseEntity<CommonResult> duplicateNickname(@PathVariable("nickname") String nickname) {
        return userService.duplicateNickname(nickname);
    }

    @PostMapping("/isExist/phone")
    public ResponseEntity<CommonResult> duplicatePhone(@RequestBody UserDuplicatePhoneRequest request){
        return userService.duplicateUserPhone(request);
    }

    @PostMapping("/isExist/uid")
    public ResponseEntity<CommonResult> duplicateUserId(@RequestBody UserDuplicateIdRequest request){
        return userService.duplicateUserId(request);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return userService.test();
    }


    @PostMapping(value = "/image", consumes = {"multipart/form-data"})
    public ResponseEntity<CommonResult> setProfilePic(@ModelAttribute UserProfileRequest request) throws IOException {
        return userService.setProfilePic(request);
    }

    @PostMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestBody UserInfoRequest request) {
        return userService.getUserInfo(request);
    }

    // 내 기록 보기
    @PostMapping(value = "/history")
    public ResponseEntity<CommonResult> getUserHistory(@RequestBody UserHistoryRequest request) throws IOException {
        log.info("getUserHistory 내부로 들어옴");
        return userService.getUserHistory(request);
    }

    // 내 기록 상세보기
    @PostMapping(value = "/history/detail")
    public ResponseEntity<CommonResult> getUserHistoryDetail(@RequestBody UserHistoryDetailRequest request) throws IOException {
        log.info("getUserHistoryDetail 내부로 들어옴");
        return userService.getUserHistoryDetail(request);
    }
}
