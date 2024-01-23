package com.ssafy.relpl.controller.rest;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@GetMapping("/rank/{rankingTime}")
public class ResponseEntity<?> getRanking(@PathVariable String rankingTime) {
    SingleResult<Ranking>
}