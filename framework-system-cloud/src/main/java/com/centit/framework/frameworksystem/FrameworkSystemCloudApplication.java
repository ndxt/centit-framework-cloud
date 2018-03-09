package com.centit.framework.frameworksystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class FrameworkSystemCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrameworkSystemCloudApplication.class, args);
	}
}
