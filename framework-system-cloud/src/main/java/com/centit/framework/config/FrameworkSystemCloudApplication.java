package com.centit.framework.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan(basePackages="com.centit.framework")
//@PropertySource(value = "classpath:application.yml", ignoreResourceNotFound = true)
public class FrameworkSystemCloudApplication extends WebMvcConfigurerAdapter implements ApplicationContextAware {


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
        ApplicationBaseConfig.setApplicationContext(applicationContext, fastJsonHttpMessageConverter);
    }


    public static void main(String[] args) {
        SpringApplication.run(FrameworkSystemCloudApplication.class, args);
    }
}
