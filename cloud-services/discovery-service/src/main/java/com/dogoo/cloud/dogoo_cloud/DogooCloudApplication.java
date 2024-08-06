package com.dogoo.cloud.dogoo_cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class DogooCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(DogooCloudApplication.class, args);
	}

}
