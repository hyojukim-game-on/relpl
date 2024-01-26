package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.db.postgre.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserLoginResponse {
    private Long userId;
    private String userNickname ;
    private int userTotalCoin ;
    private int userTotalDistance ;
    private String userPhone ;
    private int userTotalReport ;
    private String userImage ;
    private String refreshToken;
    private String accessToken;

    public static UserLoginResponse createUserLoginResponse(User user) {
        return UserLoginResponse.builder()
                .userId(user.getUserId())
                .userNickname(user.getUserNickname())
                .userTotalCoin(10000)
                .userTotalDistance(1500)
                .userPhone(user.getUserPhone())
                .userTotalReport(10)
                .userImage(user.getUserImage())
                .refreshToken("refreshToken")
                .accessToken("accessToken")
                .build();
    }
}
