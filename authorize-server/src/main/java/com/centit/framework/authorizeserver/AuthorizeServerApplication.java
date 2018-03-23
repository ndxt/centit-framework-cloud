package com.centit.framework.authorizeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan(basePackages="com.centit.framework")
public class AuthorizeServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizeServerApplication.class, args);
	}
}
