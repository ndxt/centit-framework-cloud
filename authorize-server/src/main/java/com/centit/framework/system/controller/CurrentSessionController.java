package com.centit.framework.system.controller;

import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CurrentSessionController extends BaseController {

//    @Autowired
//    protected CentitSessionRegistry centitSessionRegistry;

    @GetMapping( "/oauthUser")
    //@WrapUpResponseBody
    public String getOAuthUserInfo(OAuth2Authentication user) {
        return ResponseData.makeResponseData(user.getPrincipal()).toString();
    }

    @GetMapping( "/loginUser")
    //@WrapUpResponseBody
    public String getLoginUserInfo(HttpServletRequest request) {
        String token = request.getHeader("x-auth-token");
        ResponseMapData res = new ResponseMapData();
        res.addResponseData("x-auth-token",token );
        res.addResponseData("userInfo",WebOptUtils.getLoginUser(request) );
        return res.toString();
    }
}
