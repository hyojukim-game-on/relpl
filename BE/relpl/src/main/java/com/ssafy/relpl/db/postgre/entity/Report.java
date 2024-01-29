package com.ssafy.relpl.db.postgre.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.geo.Point;


@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 생성 전략을 설정해주어야 합니다.

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "report_id")
    private String reportId;

    @Column(name = "report_date")
    private String reportDate;

//    @Column(name = "report_content") // 내가 이걸 왜 넣었을까??
//    private String reportContent; // 내가 이걸 왜 넣었을까??
    @Column(name = "report_coordinate")
    private Point reportCoordinate;

    @Column(name = "tmap_id")
    private String tmapId;


    // 기본 생성자
//    public Report() {
//    }

}
