package com.centit.framework.cloud;

import com.centit.framework.common.ResponseJSON;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.staticsystem.po.RoleInfo;
import com.centit.framework.staticsystem.po.UserInfo;
import com.centit.framework.staticsystem.security.StaticCentitUserDetails;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class CloudSessionManagerImpl implements SessionManager {

    @Autowired
    RestTemplate restTemplate;

    private static String AUTHORIZE_SERVICE_URL="http://AUTHORIZE-SERVICE";

    @Override
    //@HystrixCommand(fallbackMethod = "createAnonymousUser")
    public CentitUserDetails getUserByToken(String userToken){
        String jsonString =
                restTemplate.getForObject(AUTHORIZE_SERVICE_URL + "/user/" + userToken,
                        String.class);
        ResponseJSON responseJSON = ResponseJSON.valueOfJson(jsonString);
        return responseJSON.getDataAsObject(StaticCentitUserDetails.class);
    }

    public CentitUserDetails createAnonymousUser(){
        StaticCentitUserDetails userDetails = new StaticCentitUserDetails();
        UserInfo userInfo = new UserInfo(
                "anonymousUser",
                "T",
                "anonymousUser",
                "anonymousUser");
        userDetails.setUserInfo(userInfo);
        List<RoleInfo> roles = new ArrayList<>(2);
        RoleInfo roleInfo = new RoleInfo("anonymous", "匿名用户角色","G",
                "U00001","T","匿名用户角色");
        roles.add(roleInfo);
        userDetails.setAuthoritiesByRoles(roles);
        return userDetails;
    }
}
