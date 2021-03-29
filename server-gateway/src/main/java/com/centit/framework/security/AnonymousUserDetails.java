package com.centit.framework.security;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.UserInfo;


import java.util.ArrayList;
import java.util.List;

public abstract class AnonymousUserDetails {
    public static CentitUserDetails createAnonymousUser(){
        JsonCentitUserDetails userDetails = new JsonCentitUserDetails();
        UserInfo userInfo = new UserInfo();

        userInfo.setUserCode("anonymousUser");
        userInfo.setUserName("anonymousUser");
        userInfo.setUserDesc("匿名用户");

        userDetails.setUserInfo((JSONObject) JSON.toJSON(userInfo));
        List<RoleInfo> roles = new ArrayList<>(2);
        RoleInfo roleInfo = new RoleInfo("anonymous", "匿名用户角色","G",
               "U00001","T","匿名用户角色");
        roles.add(roleInfo);
        userDetails.setAuthoritiesByRoles((JSONArray) JSON.toJSON(roles));
        return userDetails;
    }
}
