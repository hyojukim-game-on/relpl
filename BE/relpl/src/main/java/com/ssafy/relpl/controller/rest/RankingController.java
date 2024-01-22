package com.example.demo.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@GetMapping("/rank/{rankingTime}")
public ResponseEntity<LocalDate> getRanking(@PathVariable String rankingTime) {

    // rankingTime 문자열을 LocalDate 객체로 파싱
    LocalDate date = LocalDate.parse(rankingTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    // 비즈니스 로직(Service) 호출 및 처리

    // 조회된 랭킹 정보와 함께 LocalDate 객체 반환
    return
}