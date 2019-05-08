package com.centit.framework.servergateway;

import com.centit.framework.security.CloudFilterSecurityInterceptor;
import com.centit.framework.security.PretreatmentAuthenticationProcessingFilter;
import com.centit.framework.security.TokenAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfLogoutHandler;

@Configuration
@EnableWebSecurity
@ConditionalOnMissingClass("org.jasig.cas.client.session.SingleSignOutFilter")
public class WebSecurityDaoConfig extends WebSecurityBaseConfig {



    @Value("${login.failure.targetUrl:}")
    String defaultFailureTargetUrl;
    @Value("${login.failure.writeLog:false}")
    boolean loginFailureWritelog;



    @Value("${login.success.targetUrl:}")
    String defaultSuccessTargetUrl;
    @Value("${login.success.writeLog:true}")
    boolean loginSuccessWritelog;



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


    @Value("${access.resource.must.be.audited:false}")
    boolean accessResourceMustBeAudited;




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
