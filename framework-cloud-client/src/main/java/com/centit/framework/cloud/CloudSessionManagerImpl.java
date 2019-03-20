package com.centit.framework.cloud;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.staticsystem.po.RoleInfo;
import com.centit.framework.staticsystem.po.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class CloudSessionManagerImpl implements SessionManager {

    @Autowired
    RestTemplate restTemplate;

    @Value("${serives.authorize.url:http://AUTHORIZE-SERVICE}")
    private String AUTHORIZE_SERVICE_URL="http://AUTHORIZE-SERVICE";

    @Override
    //@HystrixCommand(fallbackMethod = "createAnonymousUser")
    public CentitUserDetails getUserByToken(String userToken){
        String jsonString =
                restTemplate.getForObject(AUTHORIZE_SERVICE_URL + "/user/" + userToken,
                        String.class);
        HttpReceiveJSON httpReceiveJSON = HttpReceiveJSON.valueOfJson(jsonString);
        return httpReceiveJSON.getDataAsObject(JsonCentitUserDetails.class);
    }

    public static CentitUserDetails createAnonymousUser(){
        JsonCentitUserDetails userDetails = new JsonCentitUserDetails();
         UserInfo userInfo = new UserInfo(
                "anonymousUser",
                "T",
                "anonymousUser",
                "anonymousUser");
        userDetails.setUserInfo((JSONObject) JSON.toJSON(userInfo));
        List<RoleInfo> roles = new ArrayList<>(2);
        RoleInfo roleInfo = new RoleInfo("anonymous", "匿名用户角色","G",
                "U00001","T","匿名用户角色");
        roles.add(roleInfo);
        userDetails.setAuthoritiesByRoles((JSONArray) JSON.toJSON(roles));
        return userDetails;
    }
}
