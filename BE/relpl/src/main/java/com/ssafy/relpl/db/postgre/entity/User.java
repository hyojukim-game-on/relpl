package com.ssafy.relpl.db.postgre.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ruser")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; // 기본키의 자료형을 변경

    // 다대일(N:1) 양방향 관계에서 여러 개의 Report 엔티티를 갖도록 설정
    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private List<Report> report;
}
