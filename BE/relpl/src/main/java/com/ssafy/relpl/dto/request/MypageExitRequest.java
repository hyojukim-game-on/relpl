package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MypageExitRequest {
    private String userUid;
    private String userPassword;
}
