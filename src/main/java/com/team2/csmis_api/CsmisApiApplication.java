package com.team2.csmis_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CsmisApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsmisApiApplication.class, args);
	}

}
