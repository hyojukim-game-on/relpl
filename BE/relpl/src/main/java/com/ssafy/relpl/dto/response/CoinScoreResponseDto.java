package com.ssafy.relpl.dto.response;

import lombok.Data;

@Data
public class CoinScoreResponseDto {
    private String coinEventDate; // 이벤트 발생 날짜, yyyy-MM-dd hh:mm
    private int coinAmount; // 단일 포인트 액수
    private String coinEventDetail; // 상세 내용
}
