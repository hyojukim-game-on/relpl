package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.db.postgre.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSignupResponse {
    private Long userId;
    private String userNickname;

    public static UserSignupResponse createUserSignupResponse(User user) {
        return  UserSignupResponse.builder()
                .userId(user.getUserId())
                .userNickname(user.getUserNickname())
                .build();
    }
}
