package com.gemsflare.gemsflare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GemsflareApplication {

	public static void main(String[] args) {
		SpringApplication.run(GemsflareApplication.class, args);
	}

}
