package com.ssafy.relpl.db.postgre.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;


@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 생성 전략을 설정해주어야 합니다.
    private Long id;
    private String reportId;
    private String reportDate;
    private Point reportCoordinate;
    private String tmapId;



    // 기본 생성자
    public Report() {
    }

}
