package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {
    private String userUid;
    private String userPassword;
}
