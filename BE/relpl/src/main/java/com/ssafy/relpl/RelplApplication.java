package com.ssafy.relpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RelplApplication {
	public static void main(String[] args) {
		SpringApplication.run(RelplApplication.class, args);
	}

}
