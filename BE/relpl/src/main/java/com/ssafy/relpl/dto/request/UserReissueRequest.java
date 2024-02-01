package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReissueRequest {
    private Long userId;
    private String accessToken;
    private String refreshToken;
}
