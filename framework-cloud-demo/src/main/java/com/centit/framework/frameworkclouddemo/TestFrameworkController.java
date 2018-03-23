package com.centit.framework.frameworkclouddemo;

import com.centit.framework.cloud.SessionManager;
import com.centit.framework.common.ResponseMapData;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.security.model.CentitUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestFrameworkController {

    @Autowired
    PlatformEnvironment platformEnvironment;

    @Autowired
    SessionManager sessionManager;

    @GetMapping(value = "/users")
    public List<? extends IUserInfo> testFramework(){
        return platformEnvironment.listAllUsers();
    }

    @GetMapping( "/user/{token}")
    public ResponseMapData getUserInfoByToken(@PathVariable String token) {

        ResponseMapData resData = new ResponseMapData();
        CentitUserDetails ud =sessionManager.getUserByToken(token);
        resData.addResponseData("userInfo", ud);

        return resData;//.toJSONString();
    }
}
