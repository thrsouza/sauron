package com.github.thrsouza.sauron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SauronApplication {

	public static void main(String[] args) {
		SpringApplication.run(SauronApplication.class, args);
	}
}
