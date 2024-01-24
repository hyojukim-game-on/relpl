package com.ssafy.relpl.db.postgre.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssafy.relpl.util.code.JoinCode;
import com.ssafy.relpl.util.code.MFCode;
import com.ssafy.relpl.util.code.YNCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user",
    uniqueConstraints = {
            @UniqueConstraint(
                    columnNames={"uid"}
            )
    }
)
// 회원 테이블
public class User extends BaseEntity{

    // User 테이블의 키값 = 회원의 고유 키값
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 회원이 가입한 타입 (none:일반, sns:소셜회원가입)
//    @Convert(converter = JoinCodeConverter.class)
    @Column(nullable = false, length = 5)
    private JoinCode joinType;

    // 회원아이디(일반:아이디, 소셜회원가입:발급번호)
    @Column(nullable = false, unique = true, length = 120)
    private String uid;

    // 비밀번호
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 255)
    private String password;

    // 회원 이름
    @Column(nullable = false, length = 32)
    private String name;

    // 회원 닉네임
    @Column(nullable = false, length = 64)
    private String nickname;

    // 회원 이메일
    @Column(nullable = false, length = 255)
    private String email;

    // 회원 휴대폰
    @Column(nullable = true, length = 32)
    private String phone;

    // 성별 (남자 M, 여자 F)
//    @Convert(converter = MFCodeConverter.class)
    @Column(nullable = false, length = 1, columnDefinition = "varchar(1) default ''")
    private MFCode gender;

    // 회원 나이
    @Column(nullable = false, length = 3, columnDefinition = "int(3) default 0")
    private int age;

    // 회원 프로필 이미지
    @Column(nullable = false, length = 255)
    private String img;

    // 주소
    @Column(length = 255)
    private String address;

    // 상세주소
    @Column(length = 255)
    private String addressDetail;

    // 푸쉬알림 설정 (Y:on, N:off)
//    @Convert(converter = YNCodeConverter.class)
    @Column(nullable = false, length = 1, columnDefinition = "varchar(1) default 'Y'")
    private YNCode push;

    // 장비 푸시용 토큰
    @Column(length = 255)
    private String token;

    // 사용 여부
//    @Convert(converter = YNCodeConverter.class)
    @Column(nullable = false, length = 1, columnDefinition = "varchar(1) default 'Y'")
    private YNCode isBind;



    // =================================================================================================
    // JWT
    // =================================================================================================
//    @Column(length = 100)
//    private String provider;
//
//    @ElementCollection(fetch = FetchType.EAGER)
//    @Builder.Default
//    private List<String> roles = new ArrayList<>();


    // =================================================================================================



    public void updateImg(String img){
        this.img = img;
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void updateAge(int age){
        this.age = age;
    }

    public void updateGender(MFCode gender){
        this.gender = gender;
    }

    public void updateIsBind(YNCode isBind){
        this.isBind = isBind;
    }

    public void updatePush(YNCode push){
        this.push = push;
    }

    public void updateToken(String token){
        this.token = token;
    }

}
