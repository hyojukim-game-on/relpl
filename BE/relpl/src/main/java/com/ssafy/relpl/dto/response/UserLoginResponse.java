package com.ssafy.relpl.dto.response;

import com.mongodb.annotations.Sealed;
import com.ssafy.relpl.db.postgre.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserLoginResponse {
    private Long userId;
    private String userNickname;
    private int userTotalCoin;
    private int userTotalDistance;
    private String userPhone;
    private int userTotalReport;
    private String userImage;
    private String accessToken;
    private String refreshToken;

    public static UserLoginResponse createUserLoginResponse(User user, String accessToken, String refreshToken) {
        return UserLoginResponse.builder()
                .userId(user.getUserId())
                .userNickname(user.getUserNickname())
                .userTotalCoin(1000)
                .userTotalDistance(2000)
                .userPhone(user.getUserPhone())
                .userTotalReport(10)
                .userImage(user.getUserImage())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
