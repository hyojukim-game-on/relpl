package com.ssafy.relpl.dto.request;

import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.redis.entity.Role;
import lombok.Getter;
import lombok.Setter;

import static com.ssafy.relpl.db.redis.entity.Role.ROLE_ADMIN;

@Getter
@Setter
public class UserSignupRequest {
    private String userUid;
    private String userNickname;
    private String userPassword;
    private String userPhone;

    public User toEntity() {
        return User.builder()
                .userUid(this.userUid)
                .userNickname(this.userNickname)
                .userPassword(this.userPassword)
                .userPhone(this.userPhone)
                .build();
    }
}
