package com.ssafy.relpl.db.postgre.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor // 기본 생성자 추가
@Table(name= "coin")
public class Coin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coin_event_id")
    private Long coinEventId; // 포인트 이벤트 id

    @Column(name = "coin_event_date")
    private String coinEventDate; // 포인트 이벤트 발생 날짜

    @Column(name = "coin_amount")
    private int coinAmount; // 포인트 액수

    @Column(name = "coin_event_detail")
    private String coinEventDetail; // 포인트 이벤트 상세 내용

    //외래키
    @ManyToOne(fetch = FetchType.EAGER) // 다대일(N:1) 관계를 표시
    @JoinColumn(name = "user_id")
    private User user;

}
