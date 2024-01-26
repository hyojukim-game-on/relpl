package com.ssafy.relpl.db.postgre.entity;


//import jakarta.persistence.*;
import jakarta.persistence.*;
import lombok.*;

//sl j 4??
@Getter
@Setter
@Builder

@AllArgsConstructor
@NoArgsConstructor // 기본 생성자 추가


@Entity
public class Coin {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long userId; // 유저 id
    private String coinEventId; // 포인트 이벤트 id
    private String coinEventDate; // 포인트 이벤트 발생 날짜
    private int coinAmount; // 포인트 액수
    private String coinEventDetail; // 포인트 이벤트 상세 내용

//    // 기본 생성자 추가
//    public Coin() {
//    }

}
