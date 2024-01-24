package com.ssafy.relpl.dto.request;


import lombok.Data;

@Data
public class CoinBarcodeRequestDto {

    private Long userId; // VARCHAR(30) 아이디는 6자 이상 영문, 숫자, 특수문자로만 이루어진다.

}
