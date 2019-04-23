package com.centit.framework.config;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.centit.framework.core.controller.WrapUpResponseBodyReturnValueHandler;
import com.centit.framework.utils.RestRequestContextInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class ApplicationBaseConfig {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        List interceptors = template.getInterceptors();
        if (interceptors == null) {
            template.setInterceptors(Collections.singletonList(new RestRequestContextInterceptor()));
        } else {
            interceptors.add(new RestRequestContextInterceptor());
            template.setInterceptors(interceptors);
        }

        return template;
    }

    public static FastJsonHttpMessageConverter fastJsonHttpMessageConverter(){
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

    public static void setApplicationContext(ApplicationContext applicationContext, FastJsonHttpMessageConverter jsonHttpMessageConverter) throws BeansException {

        RequestMappingHandlerAdapter requestMappingHandlerAdapter =
            applicationContext.getBean(RequestMappingHandlerAdapter.class);

        List<HandlerMethodReturnValueHandler> sortedHandlers = new ArrayList<>(20);
        List<HandlerMethodReturnValueHandler> defaultHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();

        // 建议都使用框架的这个注解处理，为了提高性能可以放在最前面：
        sortedHandlers.add(new WrapUpResponseBodyReturnValueHandler(jsonHttpMessageConverter));
        sortedHandlers.addAll(defaultHandlers);
        // 下面的代码式 放到 Spring 定义的  Annotation-based 组中 排在 sortedHandlers 后面
        /*for(HandlerMethodReturnValueHandler handler : defaultHandlers ){
            sortedHandlers.add(handler);
            if(handler instanceof RequestResponseBodyMethodProcessor){
                sortedHandlers.add(new WrapUpResponseBodyReturnValueHandler(jsonHttpMessageConverter));
            }
        }*/
        requestMappingHandlerAdapter.setReturnValueHandlers(sortedHandlers);
    }
}
