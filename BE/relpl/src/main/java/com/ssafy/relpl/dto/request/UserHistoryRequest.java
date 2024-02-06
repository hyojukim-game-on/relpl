package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserHistoryRequest {
    // userId Body 로 받아옴
    private long userId;
}

