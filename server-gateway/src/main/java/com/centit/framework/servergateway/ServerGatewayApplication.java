package com.centit.framework.servergateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;


@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = "com.centit.framework",
    excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class))
public class ServerGatewayApplication {//extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(ServerGatewayApplication.class, args);
    }

}
