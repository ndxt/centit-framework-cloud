package com.centit.framework.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan(basePackages="com.centit.framework")
@PropertySource(value = "classpath:application.yml", ignoreResourceNotFound = true)
public class FrameworkSystemCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrameworkSystemCloudApplication.class, args);
	}
}
