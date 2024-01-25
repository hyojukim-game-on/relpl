package com.ssafy.relpl.db.postgre.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
//@Document(collection = "coin")
public class Coin {

    private String id;
    private String coinEventId;
    private String coinEventDate;
    private int coinAmount;
    private String coinEventDetail;

    // 로깅을 위한 Logger 객체 생성
    private static final Logger logger = LoggerFactory.getLogger(Coin.class);


}
