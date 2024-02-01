package com.ssafy.relpl.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDuplicatePhoneResponse {
    private boolean isexist;

    public static UserDuplicatePhoneResponse createUserDuplicatePhoneResponse(boolean isexist) {
        return UserDuplicatePhoneResponse.builder()
                .isexist(isexist)
                .build();
    }
}
