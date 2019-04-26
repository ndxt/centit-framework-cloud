package com.centit.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.common.ObjectException;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.core.dao.DictionaryMapUtils;
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.framework.security.model.CentitUserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/session")
public class UserSeesionController extends BaseController {

    @GetMapping(value = "/usercurrstation")
    @WrapUpResponseBody
    public Map<String,Object> getUserCurrentStaticn(HttpServletRequest request) {
        Object currentUser = WebOptUtils.getLoginUser(request);
        if(currentUser instanceof CentitUserDetails) {
            return DictionaryMapUtils.mapJsonObject(
                ((CentitUserDetails) currentUser).getCurrentStation(),
                IUserUnit.class);
        }
        throw new ObjectException(ResponseData.ERROR_SESSION_TIMEOUT, "用户没有登录或者超时，请重新登录。");
    }

    @PutMapping(value = "/setuserstation/{userUnitId}")
    @WrapUpResponseBody
    public void setUserCurrentStaticn(@PathVariable String userUnitId,
                                              HttpServletRequest request) {
        Object currentUser = WebOptUtils.getLoginUser(request);
        if(currentUser instanceof CentitUserDetails) {
            ((CentitUserDetails) currentUser).setCurrentStationId(userUnitId);
        }

        throw new ObjectException(ResponseData.ERROR_SESSION_TIMEOUT, "用户没有登录或者超时，请重新登录。");
    }

    @RequestMapping(value = "/currentuser",method = RequestMethod.GET)
    @WrapUpResponseBody
    public Object getCurrentUserDetails(HttpServletRequest request) {
        return WebOptUtils.getLoginUser(request);
    }

    @GetMapping(value = "/userstations")
    @WrapUpResponseBody
    public JSONArray listCurrentUserUnits(HttpServletRequest request) {
        Object currentUser = WebOptUtils.getLoginUser(request);
        if(currentUser==null){
            throw new ObjectException(ResponseData.ERROR_SESSION_TIMEOUT, "用户没有登录或者超时，请重新登录。");
        }
        if(currentUser instanceof CentitUserDetails) {
            return DictionaryMapUtils.mapJsonArray(
                ((CentitUserDetails)currentUser).getUserUnits(), IUserUnit.class);
        }
        return null;
    }
}
