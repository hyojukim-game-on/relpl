package com.ssafy.relpl.controller;

import com.ssafy.relpl.dto.User;
import com.ssafy.relpl.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/mongo")
public class UserController {

    @Autowired
    UserService userService;

//    @GetMapping(value = "/save")
//    public User saveUser(@RequestParam String name, @RequestParam int age) {
//        return userService.save(name, age);
//    }
    @PostMapping(value = "/save")
    public User saveUser(@RequestBody User user) {
        return userService.save(user);
    }
}
