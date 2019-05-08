package com.centit.framework.servergateway;

import com.centit.framework.security.*;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.support.algorithm.StringBaseOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@EnableConfigurationProperties(SecurityProperties.class)
public  abstract class WebSecurityBaseConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    protected CsrfTokenRepository csrfTokenRepository;


    @Autowired
    protected AuthenticationManager authenticationManager;

//    @Autowired
//    protected CentitSessionRegistry centitSessionRegistry;

    @Autowired
    protected CentitUserDetailsService centitUserDetailsService;

    @Value("${login.failure.targetUrl:}")
    String defaultFailureTargetUrl;
    @Value("${login.failure.writeLog:false}")
    boolean loginFailureWritelog;

    protected TokenAuthenticationFailureHandler createFailureHandler() {
        TokenAuthenticationFailureHandler ajaxFailureHandler = new TokenAuthenticationFailureHandler();
        //String defaultTargetUrl = env.getProperty("login.failure.targetUrl");
        ajaxFailureHandler.setDefaultFailureUrl(
            StringBaseOpt.emptyValue(defaultFailureTargetUrl,
                "/system/mainframe/login/error"));
        ajaxFailureHandler.setWriteLog(loginFailureWritelog);
        return ajaxFailureHandler;
    }

    @Value("${login.success.targetUrl:}")
    String defaultSuccessTargetUrl;
    @Value("${login.success.writeLog:true}")
    boolean loginSuccessWritelog;


    protected TokenAuthenticationSuccessHandler createSuccessHandler(CentitUserDetailsService centitUserDetailsService) {
        TokenAuthenticationSuccessHandler ajaxSuccessHandler = new TokenAuthenticationSuccessHandler();
        //String defaultTargetUrl = env.getProperty("login.success.targetUrl");
        ajaxSuccessHandler.setDefaultTargetUrl(StringBaseOpt.emptyValue(defaultSuccessTargetUrl,"/"));
//        ajaxSuccessHandler.setSessionRegistry(centitSessionRegistry);
        ajaxSuccessHandler.setWriteLog(loginSuccessWritelog);
        ajaxSuccessHandler.setUserDetailsService(centitUserDetailsService);
        return ajaxSuccessHandler;
    }

    @Value("${http.csrf.enable:false}")
    boolean httpCsrfEnable;


    @Value("${login.captcha.checkTime:0}")
    int loginCaptchaCheckTime;
    @Value("${login.captcha.checkType:0}")
    int loginCaptchaCheckType;
    @Value("${login.retry.checkType:'H'}")
    String loginRetryCheckType;
    @Value("${login.retry.maxTryTimes:0}")
    int loginRetryMaxTryTimes;
    @Value("${login.retry.lockMinites:10}")
    int loginRetryLockMinites;
    @Value("${login.retry.checkTimeTnterval:3}")
    int loginRetryCheckTimeTnterval;
    @Value("${http.filter.chain.continueBeforeSuccessfulAuthentication:false}")
    boolean httpFilterChainContinueBeforeSuccessfulAuthentication;


    protected CloudFilterSecurityInterceptor createCentitPowerFilter(
            DaoAccessDecisionManager centitAccessDecisionManagerBean,
            DaoInvocationSecurityMetadataSource centitSecurityMetadataSource) {

        CloudFilterSecurityInterceptor centitPowerFilter = new CloudFilterSecurityInterceptor();
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



}
