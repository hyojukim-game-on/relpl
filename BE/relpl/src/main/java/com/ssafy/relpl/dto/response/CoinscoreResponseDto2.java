package com.ssafy.relpl.dto.response;

// 실패 dto
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinscoreResponseDto2 {

    private Integer code;
    private String message;


}
