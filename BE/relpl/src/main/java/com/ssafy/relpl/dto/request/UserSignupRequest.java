package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequest {
    private String userUid;
    private String userNickname;
    private String userPassword;
    private String userPhone;
}
