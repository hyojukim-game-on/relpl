package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.db.postgre.entity.Coin;
import lombok.Data;

@Data
public class CoinScoreResponse {
    private String coinEventDate; // 이벤트 발생 날짜, yyyy-MM-dd hh:mm
    private int coinAmount; // 단일 포인트 액수
    private String coinEventDetail; // 상세 내용

    // 기본 생성자 추가
    public CoinScoreResponse() {
    }

    // CoinScoreResponseDto에 추가된 메서드
    public static CoinScoreResponse convertFromCoin(Coin coin) {
        CoinScoreResponse responseDto = new CoinScoreResponse();
        responseDto.setCoinEventDate(coin.getCoinEventDate());
        responseDto.setCoinAmount(coin.getCoinAmount());
        responseDto.setCoinEventDetail(coin.getCoinEventDetail());
        return responseDto;
    }

}
