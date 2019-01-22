package com.centit.framework.config;

import com.centit.framework.security.*;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfLogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    protected Environment env;

    @Autowired
    protected CsrfTokenRepository csrfTokenRepository;

    @Autowired(required = false)
    protected SessionRegistry sessionRegistry;

    @Autowired
    protected CentitUserDetailsService centitUserDetailsService;

    @Autowired
    @Qualifier("passwordEncoder")
    protected Object passwordEncoder;

    @Autowired(required = false)
    private MessageSource messageSource;

    @Override
    public void configure(WebSecurity web) throws Exception {
        String ignoreUrl = StringUtils.deleteWhitespace(env.getProperty("security.ignore.url"));
        if(StringUtils.isNotBlank(ignoreUrl)){
            String[] ignoreUrls = ignoreUrl.split(",");
            for(int i = 0; i < ignoreUrls.length; i++){
                web.ignoring().antMatchers(ignoreUrls[i]);
            }
        }
        web.httpFirewall(httpFirewall());
    }

    /**
     * FIRST(Integer.MIN_VALUE), CHANNEL_FILTER, SECURITY_CONTEXT_FILTER, CONCURRENT_SESSION_FILTER,
     * WEB_ASYNC_MANAGER_FILTER,HEADERS_FILTER,CORS_FILTER,CSRF_FILTER,LOGOUT_FILTER,X509_FILTER,
     * PRE_AUTH_FILTER,CAS_FILTER,FORM_LOGIN_FILTER,OPENID_FILTER,LOGIN_PAGE_FILTER,DIGEST_AUTH_FILTER,
     * BASIC_AUTH_FILTER,REQUEST_CACHE_FILTER,SERVLET_API_SUPPORT_FILTER,JAAS_API_SUPPORT_FILTER,
     * REMEMBER_ME_FILTER,ANONYMOUS_FILTER,SESSION_MANAGEMENT_FILTER,EXCEPTION_TRANSLATION_FILTER,
     * FILTER_SECURITY_INTERCEPTOR,SWITCH_USER_FILTER,
     LAST(
     *Integer.MAX_VALUE);
     * @param http 过滤器
     * @throws Exception 配置异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if(BooleanBaseOpt.castObjectToBoolean(env.getProperty("http.anonymous.disable"),false)) {
            http.anonymous().disable();
        }

        if(getPermitAllUrl() != null && getPermitAllUrl().length>0) {
            http.authorizeRequests().antMatchers(getPermitAllUrl()).permitAll();
        }
        if(getAuthenticatedUrl() != null && getAuthenticatedUrl().length>0) {
            http.authorizeRequests().antMatchers(getAuthenticatedUrl()).authenticated();
        }


        if(BooleanBaseOpt.castObjectToBoolean(env.getProperty("http.csrf.enable"),false)) {
            http.csrf().csrfTokenRepository(csrfTokenRepository);
        } else {
            http.csrf().disable();
        }
        AjaxAccessDeniedHandlerImpl ajaxAccessDeniedHandler = new AjaxAccessDeniedHandlerImpl();
        ajaxAccessDeniedHandler.setErrorPage("/system/exception/error/403");
        http.exceptionHandling().accessDeniedHandler(ajaxAccessDeniedHandler);

        http.httpBasic().authenticationEntryPoint(getAuthenticationEntryPoint());

        switch (getFrameOptions()){
            case "DISABLE":
                http.headers().frameOptions().disable();
                break;
            case "SAMEORIGIN":
                http.headers().frameOptions().sameOrigin();
                break;
            default:
                http.headers().frameOptions().deny();
        }

        String defaultTargetUrl = env.getProperty("login.success.targetUrl");
        http.logout().logoutSuccessUrl(StringBaseOpt.emptyValue(defaultTargetUrl,"/"));


        http
                .addFilterAt(getAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(centitPowerFilter(), FilterSecurityInterceptor.class)
                .addFilterBefore(logoutFilter(), LogoutFilter.class);

        //http.rememberMe().alwaysRemember(true);
        //http.logout().invalidateHttpSession(true);
        //http.addFilterAt( null , ConcurrentSessionFilter.class);
        String loginUrl = env.getProperty("login.failure.targetUrl");
        if(StringUtils.isBlank(loginUrl)){
            loginUrl = "/system/mainframe/login";
        }
        int maximumSessions = NumberBaseOpt.parseInteger(env.getProperty("session.concurrent.maximum"),-1);
        //添加了 ConcurrentSessionFilter
        if(maximumSessions>0 && sessionRegistry != null) {
            http.sessionManagement().maximumSessions(maximumSessions)
                    .sessionRegistry(sessionRegistry).expiredUrl(loginUrl);
        }
    }

//    protected abstract String[] getAuthenticatedUrl();
//
//    protected abstract String[] getPermitAllUrl();
//
//    protected abstract AuthenticationEntryPoint getAuthenticationEntryPoint();

    protected String getFrameOptions(){
        String frameOptions = env.getProperty("framework.x-frame-options.mode");
        frameOptions = StringBaseOpt.emptyValue(frameOptions,"deny").toUpperCase();
        return frameOptions;
    }

//    protected abstract AbstractAuthenticationProcessingFilter getAuthenticationFilter();

    protected DaoFilterSecurityInterceptor centitPowerFilter(){
        DaoFilterSecurityInterceptor interceptor = new DaoFilterSecurityInterceptor();
        AuthenticationManager authenticationManager = createAuthenticationManager();
        Assert.notNull(authenticationManager, "authenticationManager不能为空");
        AjaxAuthenticationSuccessHandler successHandler = createAjaxSuccessHandler();
        Assert.notNull(successHandler, "successHandler不能为空");
        AjaxAuthenticationFailureHandler failureHandler = createAjaxFailureHandler();
        Assert.notNull(failureHandler, "failureHandler不能为空");

        interceptor.setAuthenticationManager(authenticationManager);
        interceptor.setAccessDecisionManager(createCentitAccessDecisionManager());
        interceptor.setSecurityMetadataSource(createCentitSecurityMetadataSource());
        //访问受保护的资源 总是需要重新验证身份
        interceptor.setAlwaysReauthenticate(
                BooleanBaseOpt.castObjectToBoolean(
                        env.getProperty("access.resource.notallowed.anonymous"),false));

        return interceptor;
    }
//    protected abstract Filter logoutFilter();


    protected AuthenticationManager createAuthenticationManager() {
        AuthenticationProvider authenticationProvider = getAuthenticationProvider();
        Assert.notNull(authenticationProvider, "authenticationProvider不能为空");
        List<AuthenticationProvider> providerList = new ArrayList<>();
        providerList.add(authenticationProvider);
        return new ProviderManager(providerList);
    }

//    protected abstract AuthenticationProvider getAuthenticationProvider();

    protected AjaxAuthenticationFailureHandler createAjaxFailureHandler() {
        AjaxAuthenticationFailureHandler ajaxFailureHandler = new AjaxAuthenticationFailureHandler();
        String defaultTargetUrl = env.getProperty("login.failure.targetUrl");
        ajaxFailureHandler.setDefaultFailureUrl(
                StringBaseOpt.emptyValue(defaultTargetUrl,
                        "/system/mainframe/login/error"));
        ajaxFailureHandler.setWriteLog(
                BooleanBaseOpt.castObjectToBoolean(
                        env.getProperty("login.failure.writeLog"),false));
        return ajaxFailureHandler;
    }

    protected AjaxAuthenticationSuccessHandler createAjaxSuccessHandler() {
        AjaxAuthenticationSuccessHandler ajaxSuccessHandler = new AjaxAuthenticationSuccessHandler();
        String defaultTargetUrl = env.getProperty("login.success.targetUrl");
        ajaxSuccessHandler.setDefaultTargetUrl(StringBaseOpt.emptyValue(defaultTargetUrl,"/"));

        ajaxSuccessHandler.setWriteLog(BooleanBaseOpt.castObjectToBoolean(
                env.getProperty("login.success.writeLog"),true));
        ajaxSuccessHandler.setRegistToken(BooleanBaseOpt.castObjectToBoolean(
                env.getProperty("login.success.registToken"),false));
        ajaxSuccessHandler.setUserDetailsService(centitUserDetailsService);
        return ajaxSuccessHandler;
    }

    protected DaoAccessDecisionManager createCentitAccessDecisionManager() {
        return new DaoAccessDecisionManager();
    }

    protected DaoInvocationSecurityMetadataSource createCentitSecurityMetadataSource() {
        CentitSecurityMetadata.setIsForbiddenWhenAssigned(
                BooleanBaseOpt.castObjectToBoolean(env.getProperty("access.resource.must.be.assigned"), false));
        return new DaoInvocationSecurityMetadataSource();
    }

    private StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(BooleanBaseOpt.castObjectToBoolean(env.getProperty("http.firewall.allowSemicolon"),true));
        return firewall;
    }

//    @Override
    protected String[] getAuthenticatedUrl() {
        return new String[]{"/**"};
    }

//    @Override
    protected String[] getPermitAllUrl() {
        return new String[]{"/**/login", "/system/exception"};
    }

//    @Override
    protected AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return new AjaxAuthenticationEntryPoint("/system/mainframe/login");
    }

//    @Override
    protected AbstractAuthenticationProcessingFilter getAuthenticationFilter() {
        PretreatmentAuthenticationProcessingFilter
                pretreatmentAuthenticationProcessingFilter = new PretreatmentAuthenticationProcessingFilter();
        pretreatmentAuthenticationProcessingFilter.setAuthenticationManager(createAuthenticationManager());
        pretreatmentAuthenticationProcessingFilter.setCheckCaptchaTime(
                NumberBaseOpt.castObjectToInteger(env.getProperty("login.captcha.checkTime"),0));
        pretreatmentAuthenticationProcessingFilter.setCheckCaptchaType(
                NumberBaseOpt.castObjectToInteger(env.getProperty("login.captcha.checkType"),0));
        pretreatmentAuthenticationProcessingFilter.setRetryCheckType(
                StringBaseOpt.emptyValue( env.getProperty("login.retry.checkType"),"H"));

        pretreatmentAuthenticationProcessingFilter.setRetryMaxTryTimes(
                NumberBaseOpt.castObjectToInteger(env.getProperty("login.retry.maxTryTimes"),0));

        pretreatmentAuthenticationProcessingFilter.setRetryLockMinites(
                NumberBaseOpt.castObjectToInteger(env.getProperty("login.retry.lockMinites"),10));

        pretreatmentAuthenticationProcessingFilter.setRetryCheckTimeTnterval(
                NumberBaseOpt.castObjectToInteger(env.getProperty("login.retry.checkTimeTnterval"),3));

        pretreatmentAuthenticationProcessingFilter.setContinueChainBeforeSuccessfulAuthentication(
                BooleanBaseOpt.castObjectToBoolean(
                        env.getProperty("http.filter.chain.continueBeforeSuccessfulAuthentication"),false));
        pretreatmentAuthenticationProcessingFilter.setAuthenticationFailureHandler(createAjaxFailureHandler());
        pretreatmentAuthenticationProcessingFilter.setAuthenticationSuccessHandler(createAjaxSuccessHandler());
        String requiresAuthenticationUrl = env.getProperty("login.authentication.url");
        if(StringUtils.isNotBlank(requiresAuthenticationUrl)) {
            pretreatmentAuthenticationProcessingFilter.setRequiresAuthenticationRequestMatcher(
                    new AntPathRequestMatcher(requiresAuthenticationUrl, "POST"));
        }
        if(sessionRegistry != null) {
            ConcurrentSessionControlAuthenticationStrategy strategy =
                    new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
            strategy.setMaximumSessions(NumberBaseOpt.parseInteger(env.getProperty("session.concurrent.maximum"), -1));
            pretreatmentAuthenticationProcessingFilter.setSessionAuthenticationStrategy(strategy);
        }

        return pretreatmentAuthenticationProcessingFilter;
    }

//    @Override
    protected AuthenticationProvider getAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setUserDetailsService(centitUserDetailsService);
        if( passwordEncoder instanceof org.springframework.security.authentication.encoding.PasswordEncoder) {
            ReflectionSaltSource saltSource = new ReflectionSaltSource();
            //UserInfo.salt 盐值数据字段
            String propertyToUse = env.getProperty("login.dao.passwordEncoder.salt");
            if(StringUtils.isBlank(propertyToUse)){
                propertyToUse = "userCode";
            }
            saltSource.setUserPropertyToUse(propertyToUse);
            authenticationProvider.setSaltSource(saltSource);
        }
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        if(messageSource != null && !(messageSource instanceof DelegatingMessageSource)) {
            authenticationProvider.setMessageSource(messageSource);
        }
        return authenticationProvider;
    }

//    @Override
    protected LogoutFilter logoutFilter() {
        return new LogoutFilter("/system/mainframe/login",
                new CsrfLogoutHandler(csrfTokenRepository),
                new CookieClearingLogoutHandler("JSESSIONID","remember-me"),
                new SecurityContextLogoutHandler());
    }
}
