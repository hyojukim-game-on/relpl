package com.ssafy.relpl.dto.request;

import com.ssafy.relpl.db.postgre.entity.User;
import lombok.Getter;
import lombok.Setter;

import static com.ssafy.relpl.db.redis.entity.Role.ROLE_ADMIN;

@Getter
@Setter
public class UserLoginRequest {
    private String userUid;
    private String userPassword;
}
