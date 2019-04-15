package com.centit.product.clouddemo.controller;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.algorithm.CollectionsOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class TestFrameworkController {

    @Autowired
    PlatformEnvironment platformEnvironment;

    @GetMapping(value = "/users")
    @WrapUpResponseBody
    public List<? extends IUserInfo> testFramework(){
        return platformEnvironment.listAllUsers();
    }

    @GetMapping(value = "/sessionUser")
    @WrapUpResponseBody
    public CentitUserDetails getSessionUser(HttpServletRequest request){
        return WebOptUtils.getLoginUser(request);
    }


    @GetMapping(value = "/currentUser")
    @WrapUpResponseBody
    public Map<String, Object> testCurrentUser(){

        return CollectionsOpt.createHashMap();
    }

}
