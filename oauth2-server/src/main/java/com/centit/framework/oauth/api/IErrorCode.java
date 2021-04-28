package com.centit.framework.oauth.api;

/**
 * 封装API的错误码
 *
 * @author
 */
public interface IErrorCode {
    long getCode();

    String getMessage();
}
