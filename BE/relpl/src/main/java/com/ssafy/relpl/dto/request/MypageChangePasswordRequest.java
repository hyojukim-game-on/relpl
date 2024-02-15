package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MypageChangePasswordRequest {

    private Long userId;
    private String currentPassword;
    private String newPassword;

}

