package com.centit.framework.frameworkclouddemo;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.centit.framework.core.controller.WrapUpResponseBodyReturnValueHandler;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableHystrix
@ComponentScan(basePackages="com.centit.framework")
public class FrameworkCloudDemoApplication extends WebMvcConfigurerAdapter implements ApplicationContextAware {

    private FastJsonHttpMessageConverter fastJsonHttpMessageConverter(){
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter =
                new FastJsonHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);

        fastJsonHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setFeatures(Feature.AllowArbitraryCommas,Feature.AllowUnQuotedFieldNames,
                Feature.DisableCircularReferenceDetect);
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");

        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        return fastJsonHttpMessageConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(fastJsonHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
    }

    /**
     * 重型排序 return Value Handlers
     * @param applicationContext 应用环境上下文
     * @throws BeansException 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RequestMappingHandlerAdapter requestMappingHandlerAdapter =
            applicationContext.getBean(RequestMappingHandlerAdapter.class);

        List<HandlerMethodReturnValueHandler> sortedHandlers = new ArrayList<>(20);
        List<HandlerMethodReturnValueHandler> defaultHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();

        // 建议都使用框架的这个注解处理，为了提高性能可以放在最前面：
        sortedHandlers.add(new WrapUpResponseBodyReturnValueHandler(fastJsonHttpMessageConverter()));
        sortedHandlers.addAll(defaultHandlers);
        // 下面的代码式 放到 Spring 定义的  Annotation-based 组中 排在 sortedHandlers 后面
        /*for(HandlerMethodReturnValueHandler handler : defaultHandlers ){
            sortedHandlers.add(handler);
            if(handler instanceof RequestResponseBodyMethodProcessor){
                sortedHandlers.add(new WrapUpResponseBodyReturnValueHandler(fastJsonHttpMessageConverter()));
            }
        }*/
        requestMappingHandlerAdapter.setReturnValueHandlers(sortedHandlers);
    }
    public static void main(String[] args) {
        SpringApplication.run(FrameworkCloudDemoApplication.class, args);
    }
}
