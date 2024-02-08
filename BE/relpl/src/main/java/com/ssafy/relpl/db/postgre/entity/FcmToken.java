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

    @Column(name = "user_id")
    private Long userId;
}
