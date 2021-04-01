package com.centit.framework.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.security.model.JsonCentitUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * 此类用于从GateWay中以webflux的方式响应请求
 */
@CrossOrigin
@Component
@RequestMapping("/demo")
public class DemoController {
    @RequestMapping("/adminCall")
    @ResponseBody
    public String adminCall(WebSession session, ServerWebExchange exchange){
        System.out.println("gateway中的webSessionId:" + session.getId());
        System.out.println("获取loginValue:" + session.getAttribute("loginValue").toString());

//        WebSession webSession = exchange.getSession().block();
//
//        System.out.println("gateway中的webSessionId（exchange中取出）:" + webSession.getId());

        exchange.getSession().flatMap(
                webSession -> {
                    System.out.println("gateway中的webSessionId（exchange中取出）:" + webSession.getId());
                    return Mono.just(webSession);
                }
        ).subscribe();


        JSONObject params = new JSONObject();
        params.put("code", 0);
        params.put("msg", "adminCall~");

        return params.toJSONString();
    }

    //用户登录后，会把UserDetails对象放入context中，此接口用于测试存放的用户信息
    @RequestMapping("/whoAmI")
    @ResponseBody
    public String whoAmI(WebSession session){
        System.out.println("gateway中的webSessionId:" + session.getId());
        System.out.println("获取loginValue:" + session.getAttribute("loginValue").toString());

        return JSONObject.toJSONString(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @RequestMapping("/whoAmIFromSession")
    @ResponseBody
    public String whoAmIFromSession(WebSession session, ServerWebExchange exchange){

        System.out.println("gateway中的webSessionId:" + session.getId());

        SecurityContext securityContext = session.getAttribute("SPRING_SECURITY_CONTEXT");
        Authentication authentication = securityContext.getAuthentication();
        JsonCentitUserDetails userInfo = (JsonCentitUserDetails) authentication.getPrincipal();

        return JSONObject.toJSONString(userInfo);
    }
}
