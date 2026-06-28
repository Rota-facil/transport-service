package com.rota.facil.transport_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableSpringDataWebSupport(
		pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
)
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
