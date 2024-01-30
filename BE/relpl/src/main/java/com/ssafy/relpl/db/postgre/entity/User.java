package com.ssafy.relpl.db.postgre.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ruser")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id; // 기존 건.

    @Column(name = "user_id")
    private Long id; // 기본키의 자료형을 변경
    //TODO
}
