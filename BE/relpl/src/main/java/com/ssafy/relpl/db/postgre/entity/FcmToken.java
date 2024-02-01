package com.ssafy.relpl.db.postgre.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "fcmtoken")
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_id")
    private Long fcmId; //Fcm토큰 pk

    @Column(name = "fcm_token")
    private String fcmToken; //토큰

    // 외래키
    @ManyToOne(fetch = FetchType.EAGER) // 다대일(N:1) 관계를 표시
    @JoinColumn(name = "user_id")
    private User user;
}
