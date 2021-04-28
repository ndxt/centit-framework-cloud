package com.centit.framework.controller;


import com.alibaba.fastjson.JSONObject;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.support.algorithm.StringBaseOpt;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;

//@Controller
@Component
@RequestMapping("/mainframe")
public class CasLoginController extends BaseController {
    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/logincas",method = RequestMethod.GET)
    @WrapUpResponseBody
    public String logincascod(ServerWebExchange exchange) {
        return StringBaseOpt.castObjectToString(
            exchange.getRequest().getHeaders().get("x-auth-token"));
    }

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    @ResponseBody
    public String test(ServerWebExchange exchange) {
        return JSONObject.toJSONString("test1234");
    }
}
