package com.centit.framework.securityflux;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.io.UnsupportedEncodingException;

/**
 * 无权限访问被拒绝时的自定义处理器。如不自己处理，默认返回403错误<br>
 * 由于SpringGateWay基于WebFlux，所以SpringSecruity很多原有写法，都得改为WebFlux的方式才能生效！
 */
@Component
public class AccessDeniedHandlerWebFlux implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, AccessDeniedException e) {
        JSONObject params = new JSONObject();
        params.put("code", 500);
        params.put("msg", "您无此资源的访问权限！");

        ServerHttpResponse response = serverWebExchange.getResponse();

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
