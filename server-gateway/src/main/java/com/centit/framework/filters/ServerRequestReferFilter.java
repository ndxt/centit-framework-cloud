package com.centit.framework.filters;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServerRequestReferFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        URI uri = request.getURI();
        Authentication ud = SecurityContextHolder.getContext().getAuthentication();
        exchange.getSession().flatMap(
            webSession -> {
                String lastReferer = webSession.getAttribute("Referer");
                //让浏览器把Referer保存在cookie中
                /*response.addCookie(ResponseCookie.from("Referer", uri.toString())
                    .httpOnly(true).path("/").build());*/
                //把Referer也存一份在WebSession中
                if (ud == null && StringUtils.isBlank(lastReferer)) {
                    /*response.addCookie(ResponseCookie.from("Referer", uri.toString())
                        .httpOnly(true).path("/").build());*/
                    webSession.getAttributes().put("Referer", uri.toString());
                } else if (null != ud && uri.toString().equals(lastReferer)) {
                    webSession.getAttributes().remove("Referer");
                }
                return Mono.just(webSession);
            }
        ).subscribe();
        return chain.filter(exchange);
    }

}
