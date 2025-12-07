package com.library.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.library.manager.application",
        "com.library.manager.domain",
        "com.library.manager.boot",
        "com.library.manager.driven.repositories",
        "com.library.manager.driving.controllers"
})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
