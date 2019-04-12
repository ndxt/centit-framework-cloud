package com.centit.framework.system.controller;

import com.centit.framework.authorizeserver.OAuth2SecurityConfig;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.security.model.CentitUserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CurrentSessionController extends BaseController {

    @GetMapping( "/oauthUser")
    @WrapUpResponseBody
    public Object getOAuthUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(token.startsWith("Bearer")){
            token = token.substring(6).trim();
        }
        OAuth2Authentication auth = OAuth2SecurityConfig.resourceServerTokenServices.loadAuthentication(token);
        return auth.getPrincipal();
    }

    @GetMapping( "/loginUser")
    @WrapUpResponseBody
    public CentitUserDetails getLoginUserInfo(HttpServletRequest request) {
        //String token = request.getHeader("x-auth-token");
        return WebOptUtils.getLoginUser(request);
        //return sessionRepository.findById(token);
    }
}
