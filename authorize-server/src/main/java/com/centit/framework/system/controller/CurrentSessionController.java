package com.centit.framework.system.controller;

import com.centit.framework.common.ResponseMapData;
import com.centit.framework.core.controller.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrentSessionController extends BaseController {

//    @Autowired
//    protected CentitSessionRegistry centitSessionRegistry;

    @GetMapping( "/user/{token}")
    public ResponseMapData getUserInfoByToken(@PathVariable String token) {

        ResponseMapData resData = new ResponseMapData();
//        CentitUserDetails ud =centitSessionRegistry.getCurrentUserDetails(token);
//        resData.addResponseData("userInfo", ud);

        return resData;//.toJSONString();
    }
}
