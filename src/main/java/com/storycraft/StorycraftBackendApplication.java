package com.storycraft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EntityScan(basePackages = "com.storycraft")
@EnableJpaRepositories(basePackages = "com.storycraft")
@ComponentScan(basePackages = "com.storycraft")
public class StorycraftBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorycraftBackendApplication.class, args);
	}

}
