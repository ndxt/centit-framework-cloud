package com.centit.framework.config.demo;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 此类用于模仿各种角色对应的业务请求，用于测试登陆后的业务权限验证
 */
//@CrossOrigin
@Component
@RequestMapping("/demo")
@RestController
public class DemoController {

    @RequestMapping("adminCall")
    @ResponseBody
    public String adminCall(HttpServletRequest request){
        HttpSession session = request.getSession();
        System.out.println(session.getId());

        JSONObject params = new JSONObject();
        params.put("code", 0);
        params.put("msg", "adminCall~");

        return params.toJSONString();
    }

    @RequestMapping("user1Call")
    @ResponseBody
    public String user1Call(HttpServletRequest request){

        HttpSession session = request.getSession();
        System.out.println(session.getId());

        //UserInfo userInfo = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if("MY_TOKEN".equals(cookie.getName())){
                String loginToken = cookie.getValue();
                //userInfo = (UserInfo) redisService.get("LOGINED_SESSION_" + loginToken);

                System.out.println("从redis中获取当前登录用户信息：" + loginToken);
            }
        }

        JSONObject params = new JSONObject();
        params.put("code", 0);
        params.put("msg", "user1Call~");

        return params.toJSONString();
    }

    @RequestMapping("user2Call")
    @ResponseBody
    public String user2Call(){
        JSONObject params = new JSONObject();
        params.put("code", 0);
        params.put("msg", "user2Call~");

        return params.toJSONString();
    }

    @RequestMapping("user3Call")
    @ResponseBody
    public String user3Call(){
        JSONObject params = new JSONObject();
        params.put("code", 0);
        params.put("msg", "user3Call~");

        return params.toJSONString();
    }
}
