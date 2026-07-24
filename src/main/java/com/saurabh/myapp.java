package com.saurabh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class myapp {
	public static void main(String[] args) {
		SpringApplication.run(myapp.class, args);
	}
}