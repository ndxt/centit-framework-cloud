package com.centit.framework.oauth.exception;

import com.centit.framework.oauth.api.CommonResult;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 全局处理Oauth2抛出的异常
 *
 * @author
 */
@ControllerAdvice
public class Oauth2ExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = OAuth2Exception.class)
    public CommonResult<String> handleOauth2(OAuth2Exception e) {
        return CommonResult.failed(e.getMessage());
    }
}
