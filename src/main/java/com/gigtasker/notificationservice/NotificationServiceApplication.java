package com.gigtasker.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificationServiceApplication {

    private NotificationServiceApplication() {}

	static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
