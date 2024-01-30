package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.dto.request.UserLoginRequest;
import com.ssafy.relpl.dto.request.UserSignupRequest;
import com.ssafy.relpl.service.UserService;
import com.ssafy.relpl.service.result.CommonResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
    public ResponseEntity<CommonResult> userLogin(@RequestBody UserLoginRequest request) throws UnsupportedEncodingException {
        return userService.login(request);
    }

    @GetMapping("/test")
    public ResponseEntity<CommonResult> test() {
        CommonResult result = new CommonResult();
        result.setCode(200);
        result.setMessage("성공");
        return ResponseEntity.ok(result);
    }
}
