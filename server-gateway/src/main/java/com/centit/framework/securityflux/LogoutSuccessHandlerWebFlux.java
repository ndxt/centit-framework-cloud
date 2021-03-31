package com.centit.framework.securityflux;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.util.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.io.UnsupportedEncodingException;

/**
 * 成功登出时调用的自定义处理类<br>
 * 由于SpringGateWay基于WebFlux，所以SpringSecruity很多原有写法，都得改为WebFlux的方式才能生效！
 */
@Component
public class LogoutSuccessHandlerWebFlux implements ServerLogoutSuccessHandler {
    @Autowired
    private RedisService redisService;

    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        JSONObject params = new JSONObject();
        params.put("code", 0);
        params.put("msg", "成功登出！");

        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();

        exchange.getSession().flatMap(
                webSession -> {
                    //登出成功后，删除登录成功时添加的loginValue
                    webSession.getAttributes().remove("loginValue");

                    //经检测，登出成功时cookie信息已被清空，所以只能从WebSession中获取MY_TOKEN（登录成功时放入WebSession中的）
//                    String loginToken = response.getCookies().getFirst("MY_TOKEN").getValue();
//                    //登出成功后，删除登录成功时添加的cookie
//                    response.getCookies().remove("MY_TOKEN");

                    String loginToken = webSession.getAttribute("MY_TOKEN");

                    //若loginToken为空，说明未登录就直接点击的登出，就不用删除不存在的上次登录信息了
                    if(loginToken != null && !loginToken.isEmpty()) {
                        //把已登录的用户信息从redis中删除掉
                        redisService.remove("LOGINED_SESSION_" + loginToken);
                    }

                    return Mono.just(webSession);
                }
        ).subscribe();

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        Mono<Void> ret = null;
        try {
            ret = response.writeAndFlushWith(Flux.just(ByteBufFlux.just(response.bufferFactory().wrap(params.toJSONString().getBytes("UTF-8")))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
