package com.lql.humanresourcedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
//@Profile(value = "dev")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
