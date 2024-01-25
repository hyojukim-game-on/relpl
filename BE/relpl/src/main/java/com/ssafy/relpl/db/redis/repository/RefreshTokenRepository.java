package com.ssafy.relpl.db.redis.repository;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<String, String> {
    
}
