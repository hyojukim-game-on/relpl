package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.dto.User;
import com.ssafy.relpl.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/user")
@CrossOrigin("*")
public class UserController {

    @Autowired
    UserService userService;

//    @GetMapping(value = "/save")
//    public User saveUser(@RequestParam String name, @RequestParam int age) {
//        return userService.save(name, age);
//    }
    @PostMapping(value = "/mongo/save")
    public User saveUser(@RequestBody User user) {
        return userService.save(user);
    }
}
