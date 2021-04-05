package com.centit.framework.servergateway;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.centit.framework.components.impl.NotificationCenterImpl;
import com.centit.framework.components.impl.TextOperationLogWriterImpl;
import com.centit.framework.core.controller.MvcConfigUtil;
import com.centit.framework.model.adapter.NotificationCenter;
import com.centit.framework.model.adapter.OperationLogWriter;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.DaoInvocationSecurityMetadataSource;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.framework.system.security.DaoUserDetailsService;
import com.centit.framework.system.service.impl.DBPlatformEnvironment;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.client.RestTemplate;


@Configuration
public class SystemBeanConfiguation {

    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter(){
        return MvcConfigUtil.fastJsonHttpMessageConverter();
    }

    @Bean("passwordEncoder")
    public StandardPasswordEncoderImpl passwordEncoder() {
        return new StandardPasswordEncoderImpl();
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
    }

    @Bean
    public NotificationCenter notificationCenter() {
        NotificationCenterImpl notificationCenter = new NotificationCenterImpl();
        notificationCenter.initDummyMsgSenders();
        //notificationCenter.registerMessageSender("innerMsg",innerMessageManager);
        return notificationCenter;
    }

    @Bean
    @Lazy(value = false)
    public OperationLogWriter operationLogWriter() {
        TextOperationLogWriterImpl operationLog =  new TextOperationLogWriterImpl();
        operationLog.init();
        return operationLog;
    }

    @Bean
    public PlatformEnvironment platformEnvironment() {
        DBPlatformEnvironment platformEnvironment = new DBPlatformEnvironment();
        platformEnvironment.setSupportTenant(true);
        return platformEnvironment;
    }

    @Bean
    public CentitUserDetailsService centitUserDetailsService() {
        DaoUserDetailsService userDetailsService = new DaoUserDetailsService();
        return userDetailsService;
    }

    @Bean
    public DaoInvocationSecurityMetadataSource createCentitSecurityMetadataSource() {
        return new DaoInvocationSecurityMetadataSource();
    }
}

