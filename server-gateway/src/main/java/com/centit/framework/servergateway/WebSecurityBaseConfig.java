package com.centit.framework.servergateway;

import com.centit.framework.security.*;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.support.algorithm.StringBaseOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfTokenRepository;

public abstract class WebSecurityBaseConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    protected SecurityProperties securityProperties;

    @Autowired
    protected CsrfTokenRepository csrfTokenRepository;

    @Autowired
    protected AuthenticationManager authenticationManager;

//    @Autowired
//    protected CentitSessionRegistry centitSessionRegistry;

    @Autowired
    protected CentitUserDetailsService centitUserDetailsService;

    protected TokenAuthenticationFailureHandler createFailureHandler() {
        TokenAuthenticationFailureHandler ajaxFailureHandler = new TokenAuthenticationFailureHandler();
        //String defaultTargetUrl = env.getProperty("login.failure.targetUrl");
        ajaxFailureHandler.setDefaultFailureUrl(
            StringBaseOpt.emptyValue(securityProperties.getLogin().getFailure().getTargetUrl(),
                "/system/mainframe/login/error"));
        ajaxFailureHandler.setWriteLog(securityProperties.getLogin().getFailure().isWriteLog());
        return ajaxFailureHandler;
    }

    protected TokenAuthenticationSuccessHandler createSuccessHandler(CentitUserDetailsService centitUserDetailsService) {
        TokenAuthenticationSuccessHandler ajaxSuccessHandler = new TokenAuthenticationSuccessHandler();
        //String defaultTargetUrl = env.getProperty("login.success.targetUrl");
        ajaxSuccessHandler.setDefaultTargetUrl(StringBaseOpt.emptyValue(securityProperties.getLogin().getSuccess().getTargetUrl(),"/"));
//        ajaxSuccessHandler.setSessionRegistry(centitSessionRegistry);
        ajaxSuccessHandler.setWriteLog(securityProperties.getLogin().getSuccess().isWriteLog());
        ajaxSuccessHandler.setUserDetailsService(centitUserDetailsService);
        return ajaxSuccessHandler;
    }

    protected CloudFilterSecurityInterceptor createCentitPowerFilter(
            DaoAccessDecisionManager centitAccessDecisionManagerBean,
            DaoInvocationSecurityMetadataSource centitSecurityMetadataSource) {

        CloudFilterSecurityInterceptor centitPowerFilter = new CloudFilterSecurityInterceptor();
        centitPowerFilter.setAccessDecisionManager(centitAccessDecisionManagerBean);
        centitPowerFilter.setSecurityMetadataSource(centitSecurityMetadataSource);
        return centitPowerFilter;
    }

    protected DaoAccessDecisionManager createCentitAccessDecisionManager() {
        DaoAccessDecisionManager accessDecisionManager = new DaoAccessDecisionManager();
        //accessDecisionManager.setAllResourceMustBeAudited(accessResourceMustBeAudited);
        return accessDecisionManager;
    }

    protected DaoInvocationSecurityMetadataSource createCentitSecurityMetadataSource() {
        return new DaoInvocationSecurityMetadataSource();
    }
}
