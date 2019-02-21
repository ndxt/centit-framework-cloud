package com.centit.framework.servergateway;

import com.centit.framework.security.CloudFilterSecurityInterceptor;
import com.centit.framework.security.DaoAccessDecisionManager;
import com.centit.framework.security.DaoInvocationSecurityMetadataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    protected CsrfTokenRepository csrfTokenRepository;


    protected CloudFilterSecurityInterceptor createCentitPowerFilter(
            DaoAccessDecisionManager centitAccessDecisionManagerBean,
            DaoInvocationSecurityMetadataSource centitSecurityMetadataSource) {

        CloudFilterSecurityInterceptor centitPowerFilter = new CloudFilterSecurityInterceptor();
        centitPowerFilter.setRestTemplate(restTemplate);
        centitPowerFilter.setAccessDecisionManager(centitAccessDecisionManagerBean);
        centitPowerFilter.setSecurityMetadataSource(centitSecurityMetadataSource);
        return centitPowerFilter;
    }

    @Value("${access.resource.must.be.audited:false}")
    boolean accessResourceMustBeAudited;

    protected DaoAccessDecisionManager createCentitAccessDecisionManager() {
        DaoAccessDecisionManager accessDecisionManager = new DaoAccessDecisionManager();
        //accessDecisionManager.setAllResourceMustBeAudited(accessResourceMustBeAudited);
        return accessDecisionManager;
    }

    protected DaoInvocationSecurityMetadataSource createCentitSecurityMetadataSource() {
        return new DaoInvocationSecurityMetadataSource();
    }


    @Value("${http.csrf.enable:false}")
    boolean httpCsrfEnable;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if(httpCsrfEnable) {
            http.csrf().csrfTokenRepository(csrfTokenRepository);
        } else {
            http.csrf().disable();
        }

        //AuthenticationProvider authenticationProvider = createAuthenticationProvider();
        //AuthenticationManager authenticationManager = createAuthenticationManager(authenticationProvider);

        CloudFilterSecurityInterceptor centitPowerFilter = createCentitPowerFilter(
                createCentitAccessDecisionManager(),
                createCentitSecurityMetadataSource());


        http.addFilterBefore(centitPowerFilter, FilterSecurityInterceptor.class);
    }


}
