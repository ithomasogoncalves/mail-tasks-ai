package com.mailtasksai.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MailTasksAiBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailTasksAiBackendApplication.class, args);
	}

}
