package com.plazoleta.plazoleta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PlazoletaMsPlazoletaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlazoletaMsPlazoletaApplication.class, args);
	}

}
