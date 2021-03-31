package com.centit.framework.securityflux;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.io.UnsupportedEncodingException;

/**
 * 未登录访问资源时的处理类，若无此处理类，前端页面会弹出登录窗口<br>
 * 由于SpringGateWay基于WebFlux，所以SpringSecruity很多原有写法，都得改为WebFlux的方式才能生效！
 */
@Component
public class ServerAuthenticationEntryPointWebFlux extends HttpBasicServerAuthenticationEntryPoint /* implements ServerAuthenticationEntryPoint */{

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        JSONObject params = new JSONObject();
        params.put("code", 500);
        params.put("msg", "您还未登录！");

        ServerHttpResponse response = exchange.getResponse();

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
