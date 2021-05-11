package com.centit.framework.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.config.OAuth2ClientProperties;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.servergateway.AnonymousUserDetails;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.support.network.UrlOptUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
@RequestMapping("/frame")
public class OAuth20LoginController /*extends BaseController*/ {

    @Autowired
    protected OAuth2ClientProperties oauthProperties;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @Autowired
    private CentitUserDetailsService userDetailsService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 重定向CAS登录页面
     *
     * @param response
     */
    @ApiOperation(value = "登录", notes = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void loginOAuth2(ServerHttpResponse response) {
        Authentication ud = SecurityContextHolder.getContext().getAuthentication();
        CentitUserDetails userDetails = (JsonCentitUserDetails) ud;
        response.setStatusCode(HttpStatus.FOUND);
        if (ud == null || (userDetails != null && "anonymousUser".equals(userDetails.getUserCode()))) {
            response.getHeaders().setLocation(URI.create(oauthProperties.getAuthorizationUri() +
                "?response_type=code&client_id=" + oauthProperties.getClientId() +
                "&redirect_uri=" + oauthProperties.getRedirectUri()));
        } else {
            response.getHeaders().setLocation(URI.create(oauthProperties.getLoginIndexUri()));
        }
    }

    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    @ResponseBody
    public void callback(ServerWebExchange exchange, ServerHttpResponse response) throws IOException {
        String token = exchange.getRequest().getQueryParams().getFirst("code");
        URI tokenUrl = URI.create("http://CAS-SERVICE/cas/oauth2.0/accessToken" + "?grant_type=authorization_code" +
            "&client_id=" + oauthProperties.getClientId() + "&client_secret=" + oauthProperties.getClientSecret() +
            "&code=" + token + "&redirect_uri=" + oauthProperties.getRedirectUri()
        );
        String access_token = restTemplate.getForObject(tokenUrl, String.class);
        access_token = UrlOptUtils.splitUrlParamter(access_token).get("access_token");

        URI userUrl = URI.create("http://CAS-SERVICE/cas/oauth2.0/profile" + "?grant_type=authorization_code" +
            "&access_token=" + access_token);
        String userInfo = restTemplate.getForObject(userUrl, String.class);
        JSONObject user = JSONObject.parseObject(userInfo);
        CentitUserDetails ud =platformEnvironment.loadUserDetailsByLoginName(user.getString("id"));
        SecurityContextHolder.getContext().setAuthentication(ud);
        Map<String, String> refMap = new HashMap<>();
        exchange.getSession().flatMap(
            webSession -> {
                String referer = webSession.getAttribute("urlReferer");
                refMap.put("urlReferer", referer);
                return Mono.just(webSession);
            }
        ).subscribe();
        if (!"null".equals(refMap.get("urlReferer"))) {
            response.setStatusCode(HttpStatus.FOUND);
            response.getHeaders().setLocation(URI.create(refMap.get("urlReferer")));
        }
    }

    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    @ResponseBody
    public String getUserInfo() {
        return ResponseData.makeResponseData(SecurityContextHolder.getContext().getAuthentication()).toString();
    }

    @ApiOperation(value = "当前用户登出", notes = "当前用户登出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public String logoutOAuth2(ServerHttpResponse response) {
        SecurityContextHolder.getContext().setAuthentication(AnonymousUserDetails.createAnonymousUser());
        URI logoutUrl = URI.create(oauthProperties.getLogOutUri());
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(logoutUrl);
        return ResponseData.successResponse.toString();
    }
}
