package com.order.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.order.orderservice.repository")
@EntityScan("com.order.orderservice.entity")
@SpringBootApplication
public class OrderserviceApplication {
	public static void main(String[] args) {
		SpringApplication.run(OrderserviceApplication.class, args);
	}
}

