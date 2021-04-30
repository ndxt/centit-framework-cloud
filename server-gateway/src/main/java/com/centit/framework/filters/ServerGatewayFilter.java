package com.centit.framework.filters;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author zfg
 */
@Component
public class ServerGatewayFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(ServerGatewayFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> headers = request.getHeaders().get("x-auth-token");
        String sessionId = null;
        if (!CollectionUtils.isEmpty(headers)) {
            for (String header : headers) {
                sessionId = request.getQueryParams().getFirst(header);
                if (StringUtils.isNotBlank(sessionId)) {
                    break;
                }
            }
        }
        /*if (sessionId == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }*/
        //Authentication ud = SecurityContextHolder.getContext().getAuthentication();

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
