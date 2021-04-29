package com.centit.framework.controller;


import com.alibaba.fastjson.JSONObject;
import com.centit.framework.config.OAuth2ClientProperties;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import io.swagger.annotations.ApiOperation;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;

import java.io.IOException;

//@Controller
@Component
@RequestMapping("/frame")
public class CasLoginController extends BaseController {

    @Autowired
    protected OAuth2ClientProperties oauthProperties;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    @WrapUpResponseBody
    public String loginOAuth20(ServerWebExchange exchange) {

        return "redirect:" +
            oauthProperties.getAuthorizationUri() +
            "?response_type=code&client_id"+oauthProperties.getClientId() +
            "&redirect_uri="+oauthProperties.getRedirectUri();
    }

    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/callback",method = RequestMethod.GET)
    @WrapUpResponseBody
    public CentitUserDetails callback(ServerWebExchange exchange) throws IOException {
        String token = exchange.getRequest().getQueryParams().getFirst("code");
        CloseableHttpClient request = HttpExecutor.createHttpClient();
        HttpExecutorContext executorContext = HttpExecutorContext.create(request);
        String access_token = HttpExecutor.simpleGet(executorContext,
            oauthProperties.getAccessTokenUri(),
            CollectionsOpt.createHashMap("grant_type","authorization_code",
                "client_id", oauthProperties.getClientId(),
                "client_secret", oauthProperties.getClientSecret(),
                "code",token)
        );

        String userInfo = HttpExecutor.simpleGet(executorContext,
            oauthProperties.getUserInfoUri(),
            CollectionsOpt.createHashMap("grant_type","authorization_code",
                "access_token",access_token)
        );
        request.close();
        //
        CentitUserDetails ud =platformEnvironment.loadUserDetailsByLoginName(userInfo/*json name*/);
        SecurityContextHolder.getContext().setAuthentication(ud);
        return ud;
    }


}
