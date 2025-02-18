package com.example.epam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.example.epam.entity")
public class EpamApplication {

	public static void main(String[] args) {
		SpringApplication.run(EpamApplication.class, args);
	}

}
