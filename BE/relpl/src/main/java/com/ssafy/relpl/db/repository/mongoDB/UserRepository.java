package com.ssafy.relpl.db.repository.mongoDB;

import com.ssafy.relpl.dto.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByName(String name);
}
