package com.centit.framework.servergateway;

import com.centit.framework.config.SecureIgnoreProperties;
import com.centit.framework.config.SecurityProperties;
import com.centit.framework.securityflux.RBACServiceWebFlux;
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
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;

@Configuration
@EnableWebFluxSecurity
//ConditionalOnClass(name="org.jasig.cas.client.session.SingleSignOutFilter")
@ConditionalOnProperty(prefix = "security.login.cas", name = "enabled")
@EnableConfigurationProperties(SecurityProperties.class)
public class WebSecurityOAuth2Config extends WebSecurityBaseConfig {

    //自定义的鉴权服务，通过鉴权的才能继续访问某个请求
    @Autowired
    private RBACServiceWebFlux rbacServiceWebFlux;

    @Autowired
    private SecureIgnoreProperties secureIgnoreProperties;

    @Autowired
    private WebSessionServerRequestCache webSessionServerRequestCache;

    //security的鉴权排除列表
    private static final String[] excludedAuthPages = {
        "/frame/login",
        "/frame/currentuser",
        "/frame/callback",
        "/frame/logout",
        "/cas/oauth2.0/authorize",
        "/cas/oauth2.0/accessToken",
        "/cas/oauth2.0/profile",
        "/cas/**"
    };

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        RedirectServerAuthenticationEntryPoint loginPoint =
            new RedirectServerAuthenticationEntryPoint("/frame/login");
        loginPoint.setRequestCache(webSessionServerRequestCache);
        //http.addFilterBefore(serverRequestReferFilter, SecurityWebFiltersOrder.FIRST);
        http.authorizeExchange()
            .pathMatchers(excludedAuthPages).permitAll()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .and().authorizeExchange().pathMatchers("/**").access(rbacServiceWebFlux)//自定义的鉴权服务，通过鉴权的才能继续访问某个请求
            .anyExchange().authenticated()
            .and().httpBasic()
            //.and().formLogin().loginPage(securityProperties.getLogin().getCas().getCasHome())
            .and().formLogin().loginPage("/frame/login")
            //.authenticationEntryPoint(loginPoint)//处理未认证
            //.authenticationEntryPoint(serverAuthenticationEntryPointWebFlux)//认证入口
            .and().csrf().disable()//必须支持跨域
            .logout().logoutUrl("/frame/logout");

        return http.build();
    }

}
