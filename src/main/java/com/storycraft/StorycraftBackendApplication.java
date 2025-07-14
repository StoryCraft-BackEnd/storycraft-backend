package com.storycraft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.storycraft")
@EnableJpaRepositories(basePackages = "com.storycraft")
@ComponentScan(basePackages = "com.storycraft")
public class StorycraftBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorycraftBackendApplication.class, args);
	}

}
