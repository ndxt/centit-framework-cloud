package com.centit.framework.controller;


import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.config.OAuth2ClientProperties;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.AnonymousUserDetails;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import com.centit.support.network.UrlOptUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;

import java.io.IOException;
import java.net.URI;

@Component
@RequestMapping("/frame")
public class CasLoginController /*extends BaseController*/ {

    @Autowired
    protected OAuth2ClientProperties oauthProperties;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @Autowired
    private CentitUserDetailsService userDetailsService;

    /**
     * 重定向CAS登录页面
     * @param response
     */
    @ApiOperation(value = "登录", notes = "登录")
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public void loginOAuth2(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create(oauthProperties.getAuthorizationUri() +
            "?response_type=code&client_id=" + oauthProperties.getClientId() +
            "&redirect_uri=" + oauthProperties.getRedirectUri()));
    }

    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/callback",method = RequestMethod.GET)
    @ResponseBody
    public void callback(ServerWebExchange exchange) throws IOException {
        String token = exchange.getRequest().getQueryParams().getFirst("code");
        CloseableHttpClient request = HttpExecutor.createHttpClient();
        HttpExecutorContext executorContext = HttpExecutorContext.create(request);
        String access_token = HttpExecutor.simpleGet(executorContext,
            oauthProperties.getAccessTokenUri(),
            CollectionsOpt.createHashMap("grant_type","authorization_code",
                "client_id", oauthProperties.getClientId(),
                "client_secret", oauthProperties.getClientSecret(),
                "code", token,
                "redirect_uri", oauthProperties.getRedirectUri())
        );

        access_token = UrlOptUtils.splitUrlParamter(access_token).get("access_token");

        String userInfo = HttpExecutor.simpleGet(executorContext,
            oauthProperties.getUserInfoUri(),
            CollectionsOpt.createHashMap("grant_type","authorization_code",
                "access_token",access_token)
        );
        request.close();

        JSONObject user = JSONObject.parseObject(userInfo);
        CentitUserDetails ud =platformEnvironment.loadUserDetailsByLoginName(user.getString("id"));
        SecurityContextHolder.getContext().setAuthentication(ud);
        //return exchange.getSession(); //JSON.toJSONString(ud);
    }

    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/userinfo",method = RequestMethod.GET)
    @ResponseBody
    public String getUserInfo() {
        return ResponseData.makeResponseData(SecurityContextHolder.getContext().getAuthentication()).toString();
    }

    @ApiOperation(value = "当前用户登出", notes = "当前用户登出")
    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logoutOAuth2() {
        SecurityContextHolder.getContext().setAuthentication(AnonymousUserDetails.createAnonymousUser());
        return ResponseData.successResponse.toJSONString();
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "Hello World !";
    }
}
