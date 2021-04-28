package com.centit.framework.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages="com.centit.framework",
    excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class))
public class Oauth2AuthApplication {

  public static void main(String[] args) {
    SpringApplication.run(Oauth2AuthApplication.class, args);
  }

}
