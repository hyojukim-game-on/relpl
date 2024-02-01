package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.db.postgre.entity.Coin;
import lombok.Data;

@Data
public class CoinScoreResponseDto {
    private String coinEventDate; // 이벤트 발생 날짜, yyyy-MM-dd hh:mm
    private int coinAmount; // 단일 포인트 액수
    private String coinEventDetail; // 상세 내용

    // 기본 생성자 추가
    public CoinScoreResponseDto() {
    }

    // CoinScoreResponseDto에 추가된 메서드
    public static CoinScoreResponseDto convertFromCoin(Coin coin) {
        CoinScoreResponseDto responseDto = new CoinScoreResponseDto();
        responseDto.setCoinEventDate(coin.getCoinEventDate());
        responseDto.setCoinAmount(coin.getCoinAmount());
        responseDto.setCoinEventDetail(coin.getCoinEventDetail());
        return responseDto;
    }

}
