package com.centit.product.clouddemo.controller;

import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.utils.RestRequestContext;
import com.centit.framework.utils.RestRequestContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class TestFrameworkController {

    private static String AUTHORIZE_SERVICE_URL="http://AUTHORIZE-SERVICE";

    @Autowired
    PlatformEnvironment platformEnvironment;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping(value = "/users")
    @WrapUpResponseBody
    public List<? extends IUserInfo> testFramework(){
        return platformEnvironment.listAllUsers();
    }

    @GetMapping(value = "/sessionUser")
    @WrapUpResponseBody
    public Object getSessionUser(HttpServletRequest request){
        return WebOptUtils.getLoginUser(request);
    }


    @GetMapping(value = "/currentUser")
    @WrapUpResponseBody
    public Object testCurrentUser() {
        RestRequestContext context = RestRequestContextHolder.getContext();

        if (StringUtils.isNotBlank(context.getAuthorizationToken())){
            String jsonString = restTemplate.getForObject(AUTHORIZE_SERVICE_URL + "/oauthUser/",
                    String.class);
            HttpReceiveJSON responseJSON = HttpReceiveJSON.valueOfJson(jsonString);
            return responseJSON.getDataAsObject(JsonCentitUserDetails.class);
        } else if (StringUtils.isNotBlank(context.getSessionIdToken())){
            String jsonString = restTemplate.getForObject(AUTHORIZE_SERVICE_URL + "/loginUser/",
                    String.class);
            HttpReceiveJSON responseJSON = HttpReceiveJSON.valueOfJson(jsonString);
            return responseJSON.getDataAsObject(JsonCentitUserDetails.class);
        }
        return context;
    }

}
