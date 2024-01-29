package com.ssafy.relpl.db.redis.repository;

import org.springframework.data.repository.CrudRepository;

public interface RankingRepository<T, ID> extends CrudRepository<T, ID> {
}
