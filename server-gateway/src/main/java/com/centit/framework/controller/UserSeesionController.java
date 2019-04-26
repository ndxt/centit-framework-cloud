package com.centit.framework.controller;

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

@RestController
@RequestMapping("/session")
public class UserSeesionController extends BaseController {

    @GetMapping(value = "/usercurrposition")
    @WrapUpResponseBody
    public ResponseData getUserCurrentStaticn(HttpServletRequest request) {
        CentitUserDetails currentUser = WebOptUtils.getLoginUser(request);
        if(currentUser==null){
            return ResponseData.makeErrorMessage(ResponseData.ERROR_SESSION_TIMEOUT, "用户没有登录或者超时，请重新登录。");
        }
        return ResponseData.makeResponseData(
            DictionaryMapUtils.mapJsonObject(
                currentUser.getCurrentStation(),
                IUserUnit.class));
    }

    @PutMapping(value = "/setuserposition/{userUnitId}")
    @WrapUpResponseBody
    public ResponseData setUserCurrentStaticn(@PathVariable String userUnitId,
                                              HttpServletRequest request, HttpServletResponse response) {
        CentitUserDetails currentUser = WebOptUtils.getLoginUser(request);
        if(currentUser==null){
            return ResponseData.makeErrorMessage(ResponseData.ERROR_SESSION_TIMEOUT,"用户没有登录或者超时，请重新登录。");
        }
        currentUser.setCurrentStationId(userUnitId);
        return ResponseData.makeSuccessResponse();
    }

    @RequestMapping(value = "/currentuser",method = RequestMethod.GET)
    @WrapUpResponseBody
    public ResponseData getCurrentUserDetails(HttpServletRequest request) {
        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        if(ud==null) {
            return ResponseData.makeErrorMessageWithData(
                request.getSession().getId(),ResponseData.ERROR_UNAUTHORIZED,"No user login on current session!");
        }
        else {
            return ResponseData.makeResponseData(ud);
        }
    }
}
