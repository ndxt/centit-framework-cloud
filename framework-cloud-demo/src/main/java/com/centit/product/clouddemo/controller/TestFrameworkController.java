package com.centit.product.clouddemo.controller;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.IUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RefreshScope //实现数据的动态更新
@RequestMapping("/plat")
public class TestFrameworkController {

    @Autowired
    PlatformEnvironment platformEnvironment;

    @Value("${name}")
    private String name;

    @GetMapping(value = "/users")
    public List<? extends IUserInfo> testFramework(){
        return platformEnvironment.listAllUsers("");
    }

    //测试动态更新配置
    @GetMapping("/get")
    public String getStr() {
        return name;
    }

}
