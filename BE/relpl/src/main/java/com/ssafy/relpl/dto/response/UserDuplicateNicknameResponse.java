package com.ssafy.relpl.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDuplicateNicknameResponse {
    private boolean isexist;

    public static UserDuplicateNicknameResponse createUserDuplicateNicknameResponse(boolean isexist) {
        return UserDuplicateNicknameResponse.builder()
                .isexist(isexist)
                .build();
    }
}
