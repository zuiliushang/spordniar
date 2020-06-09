package com.spordniar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpordniarAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpordniarAppApplication.class, args);
	}

	@GetMapping("greeting")
	public String greeting() {
		return "hello spordniar";
	}
	
}
