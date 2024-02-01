package com.ssafy.relpl.dto.request;

import lombok.Data;

@Data
public class FcmTokenRequest {
    private Long userId;
    private String fcmToken;
}
