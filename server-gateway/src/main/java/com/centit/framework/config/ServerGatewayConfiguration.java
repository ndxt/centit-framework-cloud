package com.centit.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

@Configuration
public class ServerGatewayConfiguration {

    @Autowired
    protected OAuth2ClientProperties oauthProperties;

    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //配置跨域
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }

    /**
     * 重定向登录请求
     * @return
     */
    @Bean
    public RouterFunction<ServerResponse> initRouterFunction(){
        String redirectUrl = oauthProperties.getAuthorizationUri() +
            "?response_type=code&client_id=" + oauthProperties.getClientId() +
            "&redirect_uri=" + oauthProperties.getRedirectUri();
        return RouterFunctions.route()
            .GET("/frame/login",serverRequest -> ServerResponse.temporaryRedirect(URI.create(redirectUrl)).build())
            .build();
    }

    /**
     * 测试重定向登录后权限过滤请求
     * @return
     */
    @Bean
    public RouterFunction<ServerResponse> initAuthFunction(){
        return RouterFunctions.route()
            .GET("/system/mainframe/logincas",serverRequest -> ServerResponse.temporaryRedirect(URI.create("/frame/hello")).build())
            .build();
    }

    /**
     * 网关路由RouteLocator测试
     * gateway网关路由有两种配置方式:
     * 1.在配置文件yml中配置
     * 2.代码中注入RouteLocator的Bean
     *
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("path_route_20210318",
                r -> r.path("/internet")
                    .uri("https://news.baidu.com/internet")
            )
            .build();
    }

}
