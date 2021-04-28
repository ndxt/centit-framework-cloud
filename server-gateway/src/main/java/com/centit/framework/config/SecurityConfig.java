package com.centit.framework.config;


import com.centit.framework.authorization.AuthorizationManager;
import com.centit.framework.component.RestAuthenticationEntryPoint;
import com.centit.framework.component.RestfulAccessDeniedHandler;
import com.centit.framework.filters.IgnoreUrlsRemoveJwtFilter;
import com.centit.framework.securityflux.RBACServiceWebFlux;
import com.centit.support.algorithm.CollectionsOpt;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 资源服务器配置
 */
@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
@EnableOAuth2Client
public class SecurityConfig {

    private final AuthorizationManager authorizationManager;

    private final IgnoreUrlsConfig ignoreUrlsConfig;

    private final RestfulAccessDeniedHandler restfulAccessDeniedHandler;

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    private final IgnoreUrlsRemoveJwtFilter ignoreUrlsRemoveJwtFilter;

    @Autowired
    private RBACServiceWebFlux rbacServiceWebFlux;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        // 1、自定义处理JWT请求头过期或签名错误的结果
        http.oauth2ResourceServer().authenticationEntryPoint(restAuthenticationEntryPoint);
        // 2、对白名单路径，直接移除JWT请求头
        http.addFilterBefore(ignoreUrlsRemoveJwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        http.authorizeExchange()
            .pathMatchers(CollectionsOpt.listToArray(ignoreUrlsConfig.getUrls(), String.class)).permitAll() // 白名单配置
            //.anyExchange().access(authorizationManager) // 鉴权管理器配置
            .anyExchange().access(rbacServiceWebFlux) // 鉴权管理器配置
            .and().exceptionHandling().accessDeniedHandler(restfulAccessDeniedHandler) // 处理未授权
            .authenticationEntryPoint(restAuthenticationEntryPoint) // 处理未认证
            .and().csrf().disable();
        return http.build();
    }

}
