package com.centit.framework.securityflux;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.io.UnsupportedEncodingException;

/**
 * 登录失败时调用的自定义处理类
 * 由于SpringGateWay基于WebFlux，所以SpringSecruity很多原有写法，都得改为WebFlux的方式才能生效！
 */
@Component
public class LoginFailedHandlerWebFlux implements ServerAuthenticationFailureHandler {
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException e) {
        JSONObject params = new JSONObject();
        params.put("code", 500);
        params.put("msg", e.getMessage());

        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        Mono<Void> ret = null;
        try {
            ret = response.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(params.toJSONString().getBytes("UTF-8")))));
        } catch (UnsupportedEncodingException e0) {
            e0.printStackTrace();
        }
        return ret;
    }
}
