package com.ssafy.relpl.service;

import com.ssafy.relpl.dto.response.User;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserService {
    @Autowired
    UserRepository userRepository;

//    public String selectUser(String name) {
//        ObjectMapper objectMapper = new ObjectMapper();
//    }

    public User save(String name, int age) {

        User user = new User();
        user.setAge(age);
        user.setName(name);
        User saved = userRepository.save(user);

        return saved;
    }

    public User save(User user) {
        log.info(user.toString());

        User saved = new User();
        saved.setAge(user.getAge());
        saved.setName(user.getName());
        User asdf = userRepository.save(saved);
        log.info(asdf.toString());

        return asdf;
    }
}
