package com.centit.framework.servergateway;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.centit.framework.core.controller.MvcConfigUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@EnableZuulProxy
@EnableEurekaClient
@SpringBootApplication
@ComponentScan(basePackages="com.centit.framework"/*,
        excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class)*/)
public class ServerGatewayApplication extends WebMvcConfigurerAdapter implements ApplicationContextAware {

    @Autowired
    private FastJsonHttpMessageConverter fastJsonHttpMessageConverter;


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(fastJsonHttpMessageConverter);
        converters.add(new StringHttpMessageConverter());
    }

    /**
     * 重型排序 return Value Handlers
     * @param applicationContext 应用环境上下文
     * @throws BeansException 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MvcConfigUtil.setApplicationContext(applicationContext, fastJsonHttpMessageConverter);
    }

    public static void main(String[] args) {
        //com.centit.framework.system.config.SystemBeanConfig
        SpringApplication.run(ServerGatewayApplication.class, args);
    }
}
