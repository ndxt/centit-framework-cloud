package com.centit.framework.servergateway;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@ConditionalOnClass(name="org.jasig.cas.client.session.SingleSignOutFilter")
@EnableConfigurationProperties(SecurityProperties.class)
public class WebSecurityCasConfig extends WebSecurityBaseConfig {

    private ServiceProperties createCasServiceProperties() {
        ServiceProperties casServiceProperties = new ServiceProperties();
        casServiceProperties.setService(securityProperties.getLogin().getCas().getLocalHome()+"/login/cas");
        casServiceProperties.setSendRenew(false);
        return casServiceProperties;
    }

    private SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleLogoutFilter = new SingleSignOutFilter();
        singleLogoutFilter.setCasServerUrlPrefix(securityProperties.getLogin().getCas().getCasHome());
        return singleLogoutFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        // 添加单点登出过滤器
        http.addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);
    }

}
