package com.centit.framework.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.ResponseData;
import com.centit.framework.config.OAuth2ClientProperties;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.util.RedisService;
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
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
@RequestMapping("/frame")
public class OAuth20LoginController {

    private static final Logger logger = LoggerFactory.getLogger(OAuth20LoginController.class);

    @Autowired
    protected OAuth2ClientProperties oauthProperties;

    @Autowired
    protected PlatformEnvironment platformEnvironment;

    @Autowired
    private CentitUserDetailsService userDetailsService;

    //@Autowired(required = false)
    //private RedisService redisService;

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
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create(oauthProperties.getAuthorizationUri() +
            "?response_type=code&client_id=" + oauthProperties.getClientId() +
            "&redirect_uri=" + oauthProperties.getRedirectUri()));
    }

    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    @ResponseBody
    public void callback(WebSession webSession, ServerWebExchange exchange) throws IOException {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String token = exchange.getRequest().getQueryParams().getFirst("code");
       /* URI tokenUrl = URI.create("http://CAS-SERVICE/cas/oauth2.0/accessToken" + "?grant_type=authorization_code" +
            "&client_id=" + oauthProperties.getClientId() + "&client_secret=" + oauthProperties.getClientSecret() +
            "&code=" + token + "&redirect_uri=" + oauthProperties.getRedirectUri()
        );*/
        try {
            Map<String, Object> accessMap = CollectionsOpt.createHashMap("grant_type", "authorization_code",
                "client_id", oauthProperties.getClientId(),
                "client_secret", oauthProperties.getClientSecret(),
                "code", token,
                "redirect_uri", oauthProperties.getRedirectUri());
            CloseableHttpClient request = HttpExecutor.createHttpClient();
            //logger.error("---------request创建成功");
            HttpExecutorContext executorContext = HttpExecutorContext.create(request);
            String access_token = HttpExecutor.simpleGet(executorContext,
                oauthProperties.getAccessTokenUri(), accessMap
            );
            //String access_token = restTemplate.getForObject(tokenUrl, String.class);
            access_token = UrlOptUtils.splitUrlParamter(access_token).get("access_token");
            //logger.error("---------access_token" + access_token);

            /*URI userUrl = URI.create("http://CAS-SERVICE/cas/oauth2.0/profile" + "?grant_type=authorization_code" +
                "&access_token=" + access_token);
            logger.error("---------userUrl" + userUrl);
            String userInfo = restTemplate.getForObject(userUrl, String.class);*/
            String userInfo = HttpExecutor.simpleGet(executorContext,
                oauthProperties.getUserInfoUri(),
                CollectionsOpt.createHashMap("grant_type", "authorization_code",
                    "access_token", access_token)
            );
            request.close();

            JSONObject user = JSONObject.parseObject(userInfo);
            CentitUserDetails ud = platformEnvironment.loadUserDetailsByLoginName(user.getString("id"));
            //SecurityContextHolder.getContext().setAuthentication(ud);
            //redisService.set(webSession.getId(), ud, 24 * 3600L);
            webSession.getAttributes().put(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ud);
            Map<String, String> refMap = new HashMap<>();
            String referer = webSession.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
            //logger.error("---------获取SPRING_SECURITY_SAVED_REQUEST：" + referer);
            String redirectUrl = "";
            if (StringUtils.isNotBlank(referer) && referer.indexOf("?") > 0) {
                redirectUrl = referer.substring(referer.indexOf("?") + 1, referer.length());
                redirectUrl = UrlOptUtils.splitUrlParamter(redirectUrl).get("redirectUrl");
            }
            if (StringUtils.isBlank(redirectUrl)) {
                if (StringUtils.isNotBlank(referer)) {
                    referer = oauthProperties.getDefaultUri() + referer;
                }
            } else {
                referer = redirectUrl;
            }
            //logger.error("sessionid:======" + webSession.getId() + "---------------实际调用：" + referer);
            refMap.put("urlReferer", referer);
            if (null != refMap.get("urlReferer") && !"null".equals(refMap.get("urlReferer"))) {
                //logger.error("---------进入重定向：");
                String xtoken = serverHttpRequest.getCookies().getFirst("SESSION").getValue();
                //logger.error("---------xtoken：" + xtoken);
                response.setStatusCode(HttpStatus.FOUND);
                response.getHeaders().setLocation(URI.create(refMap.get("urlReferer") + "?xtoken=" + xtoken));
            }
        } catch (Exception e) {
            logger.error("callback异常：", e);
        }
    }

    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/currentuser", method = RequestMethod.GET)
    @ResponseBody
    public String getUserInfo(WebSession session, ServerWebExchange exchange) {
        //CentitUserDetails userDetails = (JsonCentitUserDetails) redisService.get(session.getId());
        CentitUserDetails userDetails = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if (userDetails == null || (null != userDetails && "anonymousUser".equals(userDetails.getUserCode()))) {
            return ResponseData.makeErrorMessage(ResponseData.ERROR_USER_NOT_LOGIN,
                "用户没有登录或者超时，请重新登录！").toString();
        }
        return ResponseData.makeResponseData(userDetails).toString();
    }

    @ApiOperation(value = "cas单点登录验证接口", notes = "cas单点登录验证接口")
    @RequestMapping(value = "/logincas", method = RequestMethod.GET)
    @ResponseBody
    public String getLogin(WebSession webSession) {
        return ResponseData.successResponse.toString();
    }

    @ApiOperation(value = "当前用户登出", notes = "当前用户登出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public String logoutOAuth2(WebSession webSession, ServerWebExchange exchange) {
        SecurityContextHolder.getContext().setAuthentication(null);
        ServerHttpResponse response = exchange.getResponse();
        //redisService.remove(webSession.getId());
        webSession.getAttributes().remove(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        URI logoutUrl = URI.create(oauthProperties.getLogOutUri());
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(logoutUrl);
        return ResponseData.successResponse.toString();
    }
}
