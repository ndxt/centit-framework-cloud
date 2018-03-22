package com.centit.framework.system.controller;

import com.centit.framework.common.ResponseMapData;
import com.centit.framework.core.controller.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/session")
public class CurrentSessionController extends BaseController {

    @GetMapping( "/user/{token}")
    public ResponseMapData getUserInfoByToken(@PathVariable String token) {

        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData("userInfo", "current User Object:"+token);

        return resData;
    }
}
