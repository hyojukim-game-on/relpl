package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.dto.response.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByName(String name);
}
