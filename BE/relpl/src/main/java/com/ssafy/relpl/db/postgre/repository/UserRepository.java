package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends JpaRepository<User, String> {
    //TODO
}
