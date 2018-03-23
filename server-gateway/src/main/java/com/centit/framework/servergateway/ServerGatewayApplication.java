package com.centit.framework.servergateway;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

@EnableZuulProxy
@EnableEurekaClient
@SpringBootApplication
@ComponentScan(basePackages="com.centit.framework"/*,
		excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class)*/)
public class ServerGatewayApplication extends WebMvcConfigurerAdapter {

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

	public static void main(String[] args) {
		//com.centit.framework.system.config.SystemBeanConfig
		SpringApplication.run(ServerGatewayApplication.class, args);
	}
}
