package com.ssafy.relpl.dto.response;

// 바코드 실패시

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CoinbarcodeResponseDto2 {

    private Integer code;
    private String message;
}
