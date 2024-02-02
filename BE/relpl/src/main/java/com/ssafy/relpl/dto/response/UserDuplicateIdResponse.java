package com.ssafy.relpl.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDuplicateIdResponse {

    private boolean isexist;

    public static UserDuplicateIdResponse createUserDuplicateIdResponse(boolean isExist) {
        return UserDuplicateIdResponse.builder()
                .isexist(isExist)
                .build();
    }
}
