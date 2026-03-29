package com.ba.nwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class NwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(NwtApplication.class, args);
	}

}
