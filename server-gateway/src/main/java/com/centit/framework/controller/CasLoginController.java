package com.centit.framework.controller;


import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.algorithm.StringBaseOpt;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ServerWebExchange;

@Controller
@RequestMapping("/mainframe")
public class CasLoginController extends BaseController {
    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/logincas",method = RequestMethod.GET)
    @WrapUpResponseBody
    public String logincascod(ServerWebExchange exchange) {
        return StringBaseOpt.castObjectToString(
            exchange.getRequest().getHeaders().get("x-auth-token"));
    }
}
