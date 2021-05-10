package com.centit.framework.filters;

import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.util.RequestUrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
        URI uri = request.getURI();
        if (RequestUrlUtils.ignoreUrl(uri)) {
            return chain.filter(exchange);
        }
        Authentication ud = SecurityContextHolder.getContext().getAuthentication();
        CentitUserDetails userDetails = (JsonCentitUserDetails) ud;
        exchange.getSession().flatMap(
            webSession -> {
                String lastReferer = webSession.getAttribute("urlReferer");
                System.out.println("sessionid:======" +webSession.getId()+"---------------开始"+lastReferer);
                //将Referer存在WebSession中
                if ((ud == null || (userDetails != null && "anonymousUser".equals(userDetails.getUserCode())))
                    && StringUtils.isBlank(lastReferer)) {
                    System.out.println("sessionid:======" +webSession.getId()+"---------------覆盖"+uri.toString());
                    webSession.getAttributes().put("urlReferer", uri.toString());
                } else if (null != ud && !"anonymousUser".equals(userDetails.getUserCode())
                    && uri.toString().equals(lastReferer)) {
                    System.out.println("sessionid:======" +webSession.getId()+"---------------删除"+uri.toString());
                    webSession.getAttributes().remove("urlReferer");
                }
                return Mono.just(webSession);
            }
        ).subscribe();
        return chain.filter(exchange);
    }

}
