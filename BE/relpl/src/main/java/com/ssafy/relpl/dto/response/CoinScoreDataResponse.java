package com.ssafy.relpl.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CoinScoreDataResponse {
    private int userTotalCoin;
    private List<CoinScoreResponse> eventList;
}