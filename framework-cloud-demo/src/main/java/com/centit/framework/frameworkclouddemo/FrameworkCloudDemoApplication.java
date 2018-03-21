package com.centit.framework.frameworkclouddemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableHystrix
@ComponentScan(basePackages="com.centit.framework")
public class FrameworkCloudDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrameworkCloudDemoApplication.class, args);
	}
}
