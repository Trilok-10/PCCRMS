package com.genc.ehr_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EhrServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EhrServiceApplication.class, args);
	}

}
