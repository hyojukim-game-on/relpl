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
@Document(collection = "coin")
public class Coin {

    private String id;
    private String coin_event_id;
    private String coin_event_date;
    private int coin_amount;
    private String coin_event_detail;

    private static final Logger logger = LoggerFactory.getLogger(Coin.class);


}
