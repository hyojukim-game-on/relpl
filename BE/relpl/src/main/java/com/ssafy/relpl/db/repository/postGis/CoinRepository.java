package com.ssafy.relpl.db.repository.postGis;

import com.ssafy.relpl.db.postgre.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import org.springframework.data.mongodb.repository.MongoRepository;
@Repository
public interface CoinRepository extends JpaRepository<Coin, String> {

    // db에 저장되는 동전 정보 class
//    Coin findByName(String id);
}
