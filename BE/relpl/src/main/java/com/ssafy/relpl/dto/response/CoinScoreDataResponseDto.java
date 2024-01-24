package com.ssafy.relpl.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CoinScoreDataResponseDto {
    private int userTotalCoin;
    private List<CoinScoreResponseDto> eventList;
}
