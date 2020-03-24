package com.centit.framework.controller;

import com.centit.framework.common.ObjectException;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.security.model.CentitUserDetails;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/mainframe")
public class CasLoginController extends BaseController {
    @ApiOperation(value = "当前登录用户", notes = "获取当前登录用户详情")
    @RequestMapping(value = "/logincas",method = RequestMethod.GET)
    @WrapUpResponseBody
    public CentitUserDetails logincascod(HttpServletRequest request, HttpServletResponse response) {
        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        if(ud==null) {
            throw new ObjectException(
                request.getSession().getId(), ResponseData.ERROR_UNAUTHORIZED,
                "No user login on current session!");
        }
        return ud;
    }
}
