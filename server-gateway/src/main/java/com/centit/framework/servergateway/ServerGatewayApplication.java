package com.centit.framework.servergateway;

//import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages="com.centit.framework"/*,
        excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class)*/)
public class ServerGatewayApplication {//extends WebMvcConfigurerAdapter {

    //@Autowired
    //private FastJsonHttpMessageConverter fastJsonHttpMessageConverter;


    /*@Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(fastJsonHttpMessageConverter);
        converters.add(new StringHttpMessageConverter());
    }*/

    @Value("${test.uri:http://httpbin.org:80}")
    String uri;

    public static void main(String[] args) {
        //com.centit.framework.system.config.SystemBeanConfig
        SpringApplication.run(ServerGatewayApplication.class, args);
    }

    /*@Bean
    public RouterFunction<ServerResponse> testFunRouterFunction() {
        RouterFunction<ServerResponse> route = RouterFunctions.route(RequestPredicates.path("/testfun"),
            request -> ServerResponse.ok().body(BodyInserters.fromValue("hello")));
        return route;
    }*/
}
