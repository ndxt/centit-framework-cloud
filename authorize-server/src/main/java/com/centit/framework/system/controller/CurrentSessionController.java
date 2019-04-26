package com.centit.framework.system.controller;

import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrentSessionController extends BaseController {

   /* @GetMapping( "/oauthUser")
    @WrapUpResponseBody
    public Object getOAuthUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(token.startsWith("Bearer")){
            token = token.substring(6).trim();
        }
        OAuth2Authentication auth = OAuth2SecurityConfig.resourceServerTokenServices.loadAuthentication(token);
        //return CollectionsOpt.createHashMap("user", auth.getUserAuthentication().getPrincipal(),
        //    "authorities", AuthorityUtils.authorityListToSet(auth.getUserAuthentication().getAuthorities()));
        return auth.getUserAuthentication().getPrincipal();
    }*/

    @GetMapping( "/oauthUser")
    @WrapUpResponseBody
    public Object getOAuthUserInfo(OAuth2Authentication user) {
        return user.getPrincipal();
    }

    /*@GetMapping( "/loginUser")
    @WrapUpResponseBody
    public CentitUserDetails getLoginUserInfo(HttpServletRequest request) {
        //String token = request.getHeader("x-auth-token");
        return WebOptUtils.getLoginUser(request);
        //return sessionRepository.findById(token);
    }*/
}
