package com.centit.framework.filters;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author zfg
 */
@Component
public class ServerGatewayFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(ServerGatewayFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Authentication ud = SecurityContextHolder.getContext().getAuthentication();
        if (null != ud) {
            CentitUserDetails userDetails = (JsonCentitUserDetails) ud;
            String correlationId = request.getHeaders().getFirst(WebOptUtils.CORRELATION_ID);
            if (StringUtils.isBlank(correlationId)) {
                correlationId = UUID.randomUUID().toString();
            }
            final String correlationRelId = correlationId;
            if (!"anonymousUser".equals(userDetails.getUserCode())) {
                request.mutate().headers(httpHeaders -> {
                    httpHeaders.add(WebOptUtils.CORRELATION_ID, correlationRelId);
                    httpHeaders.add(WebOptUtils.CURRENT_USER_CODE_TAG, userDetails.getUserCode());
                    httpHeaders.add(WebOptUtils.CURRENT_TOP_UNIT_TAG, userDetails.getTopUnitCode());
                    httpHeaders.add(WebOptUtils.CURRENT_UNIT_CODE_TAG, userDetails.getCurrentUnitCode());
                    httpHeaders.add(WebOptUtils.CURRENT_STATION_ID_TAG, userDetails.getCurrentStation().getString("userUnitId"));
                }).build();
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
