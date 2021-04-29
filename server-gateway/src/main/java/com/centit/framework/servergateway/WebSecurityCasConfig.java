package com.centit.framework.servergateway;

import com.centit.framework.securityflux.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
//ConditionalOnClass(name="org.jasig.cas.client.session.SingleSignOutFilter")
@ConditionalOnProperty(prefix = "security.login.cas", name = "enabled")
@EnableConfigurationProperties(SecurityProperties.class)
public class WebSecurityCasConfig extends WebSecurityBaseConfig {

    //自定义的鉴权服务，通过鉴权的才能继续访问某个请求
    @Autowired
    private RBACServiceWebFlux rbacServiceWebFlux;

    //登录成功时调用的自定义处理类
    @Autowired
    private LoginSuccessHandlerWebFlux loginSuccessHandlerWebFlux;

    //登录失败时调用的自定义处理类
    @Autowired
    private LoginFailedHandlerWebFlux loginFailedHandlerWebFlux;

    //成功登出时调用的自定义处理类
    @Autowired
    private LogoutSuccessHandlerWebFlux logoutSuccessHandlerWebFlux;

    //无权限访问被拒绝时的自定义处理器。如不自己处理，默认返回403错误
    @Autowired
    private AccessDeniedHandlerWebFlux accessDeniedHandlerWebFlux;

    //未登录访问资源时的处理类，若无此处理类，前端页面会弹出登录窗口
    @Autowired
    private ServerAuthenticationEntryPointWebFlux serverAuthenticationEntryPointWebFlux;

    //security的鉴权排除列表
    private static final String[] excludedAuthPages = {
        "/**"
    };

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        RedirectServerAuthenticationEntryPoint loginPoint =
            new RedirectServerAuthenticationEntryPoint(securityProperties.getLogin().getCas().getCasHome());
        //支持跨域
        /*if (securityProperties.getHttp().isCsrfEnable()) {
            http.csrf().csrfTokenRepository(serverCsrfTokenRepository);
        } else {
            http.csrf().disable();
        }*/
        http.authorizeExchange()
            .pathMatchers(excludedAuthPages).permitAll()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .and().authorizeExchange().pathMatchers("/**").access(rbacServiceWebFlux)//自定义的鉴权服务，通过鉴权的才能继续访问某个请求
            .anyExchange().authenticated()
            .and().httpBasic()
            //.and().formLogin().loginPage(securityProperties.getLogin().getCas().getCasHome())
            .and().formLogin().loginPage("/system/mainframe/login")
            .authenticationSuccessHandler(loginSuccessHandlerWebFlux)//认证成功
            .authenticationFailureHandler(loginFailedHandlerWebFlux)//登陆验证失败
            .and().exceptionHandling().accessDeniedHandler(accessDeniedHandlerWebFlux) // 处理未授权
            //.authenticationEntryPoint(loginPoint);//处理未认证
            .authenticationEntryPoint(serverAuthenticationEntryPointWebFlux)//处理未认证
            .and().csrf().disable()//必须支持跨域
            .logout().logoutUrl("/system/mainframe/logout")
            .logoutSuccessHandler(logoutSuccessHandlerWebFlux);//成功登出时调用的自定义处理类
        //http.addFilterBefore(SingleSignOutFilter(), CasAuthenticationFilter.class);
        return http.build();
    }

    private ServiceProperties createCasServiceProperties() {
        ServiceProperties casServiceProperties = new ServiceProperties();
        casServiceProperties.setService(securityProperties.getLogin().getCas().getLocalHome()+"/login/cas");
        casServiceProperties.setSendRenew(false);
        return casServiceProperties;
    }

    /*protected AuthenticationProvider getAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setUserDetailsService(centitUserDetailsService);
        casAuthenticationProvider.setServiceProperties(createCasServiceProperties());
        //casAuthenticationProvider.setTicketValidator(new Cas20ServiceTicketValidator(
        //    securityProperties.getLogin().getCas().getCasHome()));
        casAuthenticationProvider.setKey("centit-demo");
        return casAuthenticationProvider;
    }*/

    protected AuthenticationEntryPoint getAuthenticationEntryPoint() {
        ServiceProperties serviceProperties = createCasServiceProperties();
        CasAuthenticationEntryPoint casEntryPoint = new CasAuthenticationEntryPoint();
        casEntryPoint.setLoginUrl(securityProperties.getLogin().getCas().getCasHome());
        casEntryPoint.setServiceProperties(serviceProperties);
        return casEntryPoint;
    }

    protected AuthenticationManager createAuthenticationManager() {
        //AuthenticationProvider authenticationProvider = getAuthenticationProvider();
        //Assert.notNull(authenticationProvider, "authenticationProvider不能为空");
        List<AuthenticationProvider> providerList = new ArrayList<>();
        //providerList.add(authenticationProvider);
        return new ProviderManager(providerList);
    }

    /*private SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleLogoutFilter = new SingleSignOutFilter();
        //singleLogoutFilter.setCasServerUrlPrefix(
        //    securityProperties.getLogin().getCas().getCasHome());
        return singleLogoutFilter;
    }*/

    protected AbstractAuthenticationProcessingFilter getAuthenticationFilter() {
        CasAuthenticationFilter casFilter = new CasAuthenticationFilter();
        casFilter.setAuthenticationManager(createAuthenticationManager());
        casFilter.setAuthenticationFailureHandler(createFailureHandler());
        casFilter.setAuthenticationSuccessHandler(createSuccessHandler(centitUserDetailsService));


        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(false);

        casFilter.setRememberMeServices(rememberMeServices);
        return casFilter;
    }

    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http);
        if(securityProperties.getHttp().isCsrfEnable()) {
            http.csrf().csrfTokenRepository(csrfTokenRepository);
        } else {
            http.csrf().disable();
        }
        http.authorizeRequests()
            .antMatchers("/system/mainframe/login", "/system/exception", "/oauth/check_token").permitAll()
            .and().exceptionHandling().accessDeniedPage("/system/exception/error/403")
            .and().sessionManagement().invalidSessionUrl("/system/exception/error/401")
            .and().httpBasic().authenticationEntryPoint(getAuthenticationEntryPoint());

        http.headers().frameOptions().sameOrigin();

        DaoFilterSecurityInterceptor centitPowerFilter = createCentitPowerFilter(
            createCentitAccessDecisionManager(),
            createCentitSecurityMetadataSource());
        http
            .addFilterAt(getAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(centitPowerFilter, FilterSecurityInterceptor.class)
            .addFilterBefore( singleSignOutFilter(), CasAuthenticationFilter.class);
    }*/
}
