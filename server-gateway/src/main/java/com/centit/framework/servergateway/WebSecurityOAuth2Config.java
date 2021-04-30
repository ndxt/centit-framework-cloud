package com.centit.framework.servergateway;

import com.centit.framework.securityflux.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;

@Configuration
@EnableWebFluxSecurity
//ConditionalOnClass(name="org.jasig.cas.client.session.SingleSignOutFilter")
@ConditionalOnProperty(prefix = "security.login.cas", name = "enabled")
@EnableConfigurationProperties(SecurityProperties.class)
public class WebSecurityOAuth2Config extends WebSecurityBaseConfig {

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

    //security的鉴权排除列表
    private static final String[] excludedAuthPages = {
        "/frame/login",
        "/frame/callback",
        "/frame/userinfo",
        "/frame/logout",
        "/cas/**"
        //"/**"
    };

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
       /* String redirectUrl = oauthProperties.getAuthorizationUri() +
            "?response_type=code&client_id=" + oauthProperties.getClientId() +
            "&redirect_uri=" + oauthProperties.getRedirectUri();*/
        //securityProperties.getLogin().getCas().getCasHome()
        RedirectServerAuthenticationEntryPoint loginPoint =
            new RedirectServerAuthenticationEntryPoint("/frame/login");
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
            .and().formLogin().loginPage("/frame/login")
            .authenticationSuccessHandler(loginSuccessHandlerWebFlux)//认证成功
            .authenticationFailureHandler(loginFailedHandlerWebFlux)//登陆验证失败
            //.and().addFilterAt()
            .and().exceptionHandling().accessDeniedHandler(accessDeniedHandlerWebFlux) // 处理未授权
            .authenticationEntryPoint(loginPoint)//处理未认证
            //.authenticationEntryPoint(serverAuthenticationEntryPointWebFlux)//认证入口
            .and().csrf().disable()//必须支持跨域
            .logout().logoutUrl("/frame/logout")
            .logoutSuccessHandler(logoutSuccessHandlerWebFlux);//成功登出时调用的自定义处理类

        return http.build();
    }

}
