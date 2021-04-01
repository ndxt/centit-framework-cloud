package com.centit.framework.securityflux;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

/**
 * 自定义的鉴权服务，通过鉴权的才能继续访问某个请求<br>
 * 由于SpringGateWay基于WebFlux，所以SpringSecruity很多原有写法，都得改为WebFlux的方式才能生效！
 */
@Component
public class RBACServiceWebFlux implements ReactiveAuthorizationManager<AuthorizationContext> {
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
        ServerHttpRequest request = object.getExchange().getRequest();
        String uri = request.getPath().pathWithinApplication().value();

        return authentication
        .filter(a -> a.isAuthenticated())
                .flatMapIterable( a -> a.getAuthorities())
                .map( g-> g.getAuthority())
                .any(c->{
                    /*if (antPathMatcher.match("/" + c, uri)) {
                        return true;
                    }
                    return false;*/
                    String[] roles = c.split(",");
                    for(String role:roles) {
                        if("R_public".contains(role)) {
                           return true;
                        }
                    }
                    return false;
//                    object.getExchange().
//                    //检测权限是否匹配
//                    String
//                    String[] roles = c.split(",");
//                    for(String role:roles) {
//                        if(authorities.contains(role)) {
//                            return true;
//                        }
//                    }
//                    return false;
                })
                .map( hasAuthority -> new AuthorizationDecision(hasAuthority))
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        return null;
    }
}
