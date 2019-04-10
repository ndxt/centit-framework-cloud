package com.centit.framework.system.controller;

import com.centit.framework.common.ResponseData;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrentSessionController extends BaseController {

//    @Autowired
//    protected CentitSessionRegistry centitSessionRegistry;

    @GetMapping( "/user")
    //@WrapUpResponseBody
    public String getUserInfoByToken(OAuth2Authentication user) {
        return ResponseData.makeResponseData(user).toString();
    }

}
