package com.centit.framework.servergateway;

import com.centit.framework.security.*;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.support.algorithm.StringBaseOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRepository;

public abstract class WebSecurityBaseConfig {//extends WebSecurityConfigurerAdapter {

    @Autowired
    protected SecurityProperties securityProperties;

    @Autowired(required = false)
    //protected CsrfTokenRepository csrfTokenRepository;
    protected ServerCsrfTokenRepository serverCsrfTokenRepository;

    @Autowired(required = false)
    protected AuthenticationManager authenticationManager;

//    @Autowired
//    protected CentitSessionRegistry centitSessionRegistry;

    @Autowired(required = false)
    protected CentitUserDetailsService centitUserDetailsService;

    protected AjaxAuthenticationFailureHandler createFailureHandler() {
        AjaxAuthenticationFailureHandler ajaxFailureHandler = new AjaxAuthenticationFailureHandler();
        //String defaultTargetUrl = env.getProperty("login.failure.targetUrl");
        ajaxFailureHandler.setDefaultFailureUrl(
            StringBaseOpt.emptyValue(securityProperties.getLogin().getFailure().getTargetUrl(),
                "/system/mainframe/login/error"));
        ajaxFailureHandler.setWriteLog(securityProperties.getLogin().getFailure().isWriteLog());
        return ajaxFailureHandler;
    }

    protected AjaxAuthenticationSuccessHandler createSuccessHandler(CentitUserDetailsService centitUserDetailsService) {
        AjaxAuthenticationSuccessHandler ajaxSuccessHandler = new AjaxAuthenticationSuccessHandler();
        //String defaultTargetUrl = env.getProperty("login.success.targetUrl");
        ajaxSuccessHandler.setDefaultTargetUrl(StringBaseOpt.emptyValue(securityProperties.getLogin().getSuccess().getTargetUrl(),"/"));
//        ajaxSuccessHandler.setSessionRegistry(centitSessionRegistry);
        ajaxSuccessHandler.setWriteLog(securityProperties.getLogin().getSuccess().isWriteLog());
        ajaxSuccessHandler.setUserDetailsService(centitUserDetailsService);
        return ajaxSuccessHandler;
    }

    protected DaoFilterSecurityInterceptor createCentitPowerFilter(
        DaoAccessDecisionManager centitAccessDecisionManagerBean,
        DaoInvocationSecurityMetadataSource centitSecurityMetadataSource) {

        DaoFilterSecurityInterceptor centitPowerFilter = new DaoFilterSecurityInterceptor();
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
