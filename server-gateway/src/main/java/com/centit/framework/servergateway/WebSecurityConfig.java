package com.centit.framework.servergateway;

import com.centit.framework.security.*;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.support.algorithm.StringBaseOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfLogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    protected CsrfTokenRepository csrfTokenRepository;


    @Autowired
    AuthenticationManager authenticationManager;

//    @Autowired
//    protected CentitSessionRegistry centitSessionRegistry;

    @Autowired
    protected CentitUserDetailsService centitUserDetailsService;

    @Value("${login.failure.targetUrl}")
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

    @Value("${login.success.targetUrl}")
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



    private LoginUrlAuthenticationEntryPoint authenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint("/system/mainframe/login");
    }

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

    private UsernamePasswordAuthenticationFilter createPretreatmentAuthenticationProcessingFilter(
        AuthenticationManager authenticationManager,TokenAuthenticationSuccessHandler ajaxSuccessHandler,
        AuthenticationFailureHandler ajaxFailureHandler) {

        PretreatmentAuthenticationProcessingFilter
            pretreatmentAuthenticationProcessingFilter = new PretreatmentAuthenticationProcessingFilter();
        pretreatmentAuthenticationProcessingFilter.setAuthenticationManager(authenticationManager);
        pretreatmentAuthenticationProcessingFilter.setCheckCaptchaTime(
            loginCaptchaCheckTime);
        pretreatmentAuthenticationProcessingFilter.setCheckCaptchaType(
            loginCaptchaCheckType);
        pretreatmentAuthenticationProcessingFilter.setRetryCheckType(loginRetryCheckType);

        pretreatmentAuthenticationProcessingFilter.setRetryMaxTryTimes(
            loginRetryMaxTryTimes);
        pretreatmentAuthenticationProcessingFilter.setRetryLockMinites(
            loginRetryLockMinites);
        pretreatmentAuthenticationProcessingFilter.setRetryCheckTimeTnterval(
            loginRetryCheckTimeTnterval);

        pretreatmentAuthenticationProcessingFilter.setContinueChainBeforeSuccessfulAuthentication(
            httpFilterChainContinueBeforeSuccessfulAuthentication);
        pretreatmentAuthenticationProcessingFilter.setAuthenticationFailureHandler(ajaxFailureHandler);
        pretreatmentAuthenticationProcessingFilter.setAuthenticationSuccessHandler(ajaxSuccessHandler);
        return pretreatmentAuthenticationProcessingFilter;
    }

    private LogoutFilter logoutFilter() {
        return new LogoutFilter("/system/mainframe/login",
            new CsrfLogoutHandler(csrfTokenRepository),
            new CookieClearingLogoutHandler("JSESSIONID","remember-me"),
            new SecurityContextLogoutHandler());
    }

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


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if(httpCsrfEnable) {
            http.csrf().csrfTokenRepository(csrfTokenRepository);
        } else {
            http.csrf().disable();
        }
        http.authorizeRequests()
            .antMatchers("/system/mainframe/login", "/system/exception", "/oauth/check_token").permitAll()
            .and().exceptionHandling().accessDeniedPage("/system/exception/error/403")
            .and().sessionManagement().invalidSessionUrl("/system/exception/error/401")
            .and().httpBasic().authenticationEntryPoint(authenticationEntryPoint());

        http.headers().frameOptions().sameOrigin();

        //AuthenticationProvider authenticationProvider = createAuthenticationProvider();
        //AuthenticationManager authenticationManager = createAuthenticationManager(authenticationProvider);

        AuthenticationFailureHandler ajaxFailureHandler = createFailureHandler();
        TokenAuthenticationSuccessHandler ajaxSuccessHandler = createSuccessHandler(centitUserDetailsService);

        UsernamePasswordAuthenticationFilter pretreatmentAuthenticationProcessingFilter =
            createPretreatmentAuthenticationProcessingFilter(
                authenticationManager, ajaxSuccessHandler, ajaxFailureHandler);

        http.addFilterAt(pretreatmentAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(logoutFilter(), LogoutFilter.class);

        CloudFilterSecurityInterceptor centitPowerFilter = createCentitPowerFilter(
            createCentitAccessDecisionManager(),
            createCentitSecurityMetadataSource());


        http.addFilterBefore(centitPowerFilter, FilterSecurityInterceptor.class);
    }

}
