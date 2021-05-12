package com.centit.framework.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.config.OAuth2ClientProperties;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.servergateway.AnonymousUserDetails;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.network.HttpExecutor;
import com.centit.support.network.HttpExecutorContext;
import com.centit.support.network.UrlOptUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(OAuth20LoginController.class);

    @Autowired
    protected OAuth2ClientProperties oauthProperties;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @Autowired
    private CentitUserDetailsService userDetailsService;

    //@Autowired
    //private RestTemplate restTemplate;

    /**
     * 重定向CAS登录页面
     *
     * @param response
     */
    @ApiOperation(value = "登录", notes = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void loginOAuth2(ServerHttpResponse response) {
        /*Authentication ud = SecurityContextHolder.getContext().getAuthentication();
        CentitUserDetails userDetails = (JsonCentitUserDetails) ud;*/

        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create(oauthProperties.getAuthorizationUri() +
            "?response_type=code&client_id=" + oauthProperties.getClientId() +
            "&redirect_uri=" + oauthProperties.getRedirectUri()));
        /*if (ud == null || (userDetails != null && "anonymousUser".equals(userDetails.getUserCode()))) {
            response.getHeaders().setLocation(URI.create(oauthProperties.getAuthorizationUri() +
                "?response_type=code&client_id=" + oauthProperties.getClientId() +
                "&redirect_uri=" + oauthProperties.getRedirectUri()));
        } else {
            response.getHeaders().setLocation(URI.create(oauthProperties.getLoginIndexUri()));
        }*/
    }

    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    @ResponseBody
    public void callback(ServerWebExchange exchange, ServerHttpResponse response) throws IOException {
        String token = exchange.getRequest().getQueryParams().getFirst("code");
       /* URI tokenUrl = URI.create("http://CAS-SERVICE/cas/oauth2.0/accessToken" + "?grant_type=authorization_code" +
            "&client_id=" + oauthProperties.getClientId() + "&client_secret=" + oauthProperties.getClientSecret() +
            "&code=" + token + "&redirect_uri=" + oauthProperties.getRedirectUri()
        );*/
        logger.error("---------tokenUrl" + token);
        try {
            logger.error("---------getAccessTokenUri:" + oauthProperties.getAccessTokenUri());
            Map<String, Object> accessMap = CollectionsOpt.createHashMap("grant_type","authorization_code",
                "client_id", oauthProperties.getClientId(),
                "client_secret", oauthProperties.getClientSecret(),
                "code", token,
                "redirect_uri", oauthProperties.getRedirectUri());
            logger.error("---------accessMap:" + accessMap.toString());
            CloseableHttpClient request = HttpExecutor.createHttpClient();
            logger.error("---------request创建成功" );
            HttpExecutorContext executorContext = HttpExecutorContext.create(request);
            String access_token = HttpExecutor.simpleGet(executorContext,
                oauthProperties.getAccessTokenUri(),accessMap
            );
            //String access_token = restTemplate.getForObject(tokenUrl, String.class);
            access_token = UrlOptUtils.splitUrlParamter(access_token).get("access_token");
            logger.error("---------access_token" + access_token);

            /*URI userUrl = URI.create("http://CAS-SERVICE/cas/oauth2.0/profile" + "?grant_type=authorization_code" +
                "&access_token=" + access_token);
            logger.error("---------userUrl" + userUrl);
            String userInfo = restTemplate.getForObject(userUrl, String.class);*/
            String userInfo = HttpExecutor.simpleGet(executorContext,
                oauthProperties.getUserInfoUri(),
                CollectionsOpt.createHashMap("grant_type","authorization_code",
                    "access_token",access_token)
            );
            request.close();

            logger.error("---------userInfo用户信息1：", userInfo.toString());
            JSONObject user = JSONObject.parseObject(userInfo);
            logger.error("---------user用户信息2：", user);
            CentitUserDetails ud = platformEnvironment.loadUserDetailsByLoginName(user.getString("id"));
            logger.error("---------ud用户信息3：", ud);
            SecurityContextHolder.getContext().setAuthentication(ud);
            Map<String, String> refMap = new HashMap<>();
            exchange.getSession().flatMap(
                webSession -> {
                    //String referer = webSession.getAttribute("urlReferer");
                    String referer = webSession.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
                    logger.error("---------获取SPRING_SECURITY_SAVED_REQUEST：" + referer);
                    if (StringUtils.isNotBlank(referer)) {
                        referer = oauthProperties.getDefaultUri() + referer;
                    }
                    logger.error("sessionid:======" + webSession.getId() + "---------------实际调用：" + referer);
                    refMap.put("urlReferer", referer);
                    return Mono.just(webSession);
                }
            ).subscribe();
            if (null != refMap.get("urlReferer") && !"null".equals(refMap.get("urlReferer"))) {
                logger.error("---------进入重定向：");
                response.setStatusCode(HttpStatus.FOUND);
                response.getHeaders().setLocation(URI.create(refMap.get("urlReferer")));
            }
        } catch (Exception e) {
            logger.error("callback异常：", e);
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
    public String logoutOAuth2(ServerWebExchange exchange, ServerHttpResponse response) {
        SecurityContextHolder.getContext().setAuthentication(AnonymousUserDetails.createAnonymousUser());
        /*exchange.getSession().flatMap(
            webSession -> {
                webSession.getAttributes().remove("urlReferer");
                return Mono.just(webSession);
            }
        ).subscribe();*/
        URI logoutUrl = URI.create(oauthProperties.getLogOutUri());
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(logoutUrl);
        return ResponseData.successResponse.toString();
    }
}
