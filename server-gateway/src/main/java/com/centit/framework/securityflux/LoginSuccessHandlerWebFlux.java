package com.centit.framework.securityflux;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.bean.UserInfoWithSecurity;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.util.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;

import java.io.UnsupportedEncodingException;

/**
 * 登录成功时调用的自定义处理类<br>
 * 由于SpringGateWay基于WebFlux，所以SpringSecruity很多原有写法，都得改为WebFlux的方式才能生效！
 */
@Component
public class LoginSuccessHandlerWebFlux implements ServerAuthenticationSuccessHandler {
    @Autowired
    private RedisService redisService;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        JSONObject params = new JSONObject();
        params.put("code", 0);
        params.put("msg", "登陆成功！");

        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();

        UserInfoWithSecurity uws = (UserInfoWithSecurity) authentication.getPrincipal();
        UserInfo user = uws.getUserInfo();

        exchange.getSession().flatMap(
                webSession -> {
                    //上次登录时遗留的token
                    String lastLoginToken = webSession.getAttribute("MY_TOKEN");
                    System.out.println("上次登录时遗留的token：" + lastLoginToken);

                    //若上次遗留token还在，说明上次未正常退出，则删除遗留信息
                    if(lastLoginToken != null && !lastLoginToken.isEmpty()) {
                        //把已登录的用户信息从redis中删除掉
                        redisService.remove("LOGINED_SESSION_" + lastLoginToken);
                    }

                    System.out.println("登录时gateway中的webSessionId（exchange中取出）:" + webSession.getId());
                    webSession.getAttributes().put("loginValue", "111");
                    //把seesionId当做我们的登录信息token使用，让浏览器保存在cookie中
                    response.addCookie(ResponseCookie.from("MY_TOKEN", webSession.getId())
                            .httpOnly(true).path("/").build());
                    //把登录信息token也存一份在WebSession中（登出成功时使用）
                    webSession.getAttributes().put("MY_TOKEN", webSession.getId());

                    //把用户信息放入redis中，最长存在时间24小时（防止用户未合法退出时留存到redis中）
                    redisService.set("LOGINED_SESSION_" + webSession.getId(), user, 24*3600L);

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
