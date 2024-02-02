package com.ssafy.relpl.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserReissueResponse {
    private String accessToken;
    private String refreshToken;

    public static UserReissueResponse createUserReissueResponse(String accessToken, String refreshToken) {
        return UserReissueResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}