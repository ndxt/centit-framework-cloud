package com.centit.framework.security;


import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.system.security.CentitUserDetailsImpl;

import java.util.ArrayList;
import java.util.List;

public abstract class AnonymousUserDetails {
    public static CentitUserDetails createAnonymousUser(){
        CentitUserDetailsImpl userDetails = new CentitUserDetailsImpl();
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
