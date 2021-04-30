package com.centit.framework.oauth.component;

import com.centit.framework.oauth.service.principal.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

//import com.centit.framework.security.model.JsonCentitUserDetails;


/**
 * JWT内容增强器
 *
 * @author
 */
@Component
public class JwtTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Authentication userAuthentication = authentication.getUserAuthentication();
        if (userAuthentication != null) {
            //JsonCentitUserDetails centitUserDetails = (JsonCentitUserDetails) authentication.getPrincipal();
            UserPrincipal centitUserDetails = (UserPrincipal) authentication.getPrincipal();
            Map<String, Object> info = new HashMap<>();
            // 把用户ID设置到JWT中
            //info.put("id", userPrincipal.getId());
            //把用户标识以userDetails这个Key加入到JWT的额外信息中去
            info.put("userDetails", centitUserDetails);
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        }
        return accessToken;
    }
}
