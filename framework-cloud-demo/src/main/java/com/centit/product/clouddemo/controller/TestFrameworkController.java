package com.centit.product.clouddemo.controller;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestFrameworkController {

    @Autowired
    PlatformEnvironment platformEnvironment;

    @GetMapping(value = "/users")
    public List<? extends IUserInfo> testFramework(){
        return platformEnvironment.listAllUsers();
    }

}
