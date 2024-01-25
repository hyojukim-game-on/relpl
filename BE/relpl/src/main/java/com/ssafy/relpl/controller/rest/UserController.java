package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.dto.request.UserSignupRequest;
import com.ssafy.relpl.service.UserService;
import com.ssafy.relpl.service.result.CommonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
