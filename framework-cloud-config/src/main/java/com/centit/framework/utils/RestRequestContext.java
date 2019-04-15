package com.centit.framework.utils;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RestRequestContext {

    public static final String CORRELATION_ID = "cnt-correlation-id";
    public static final String USER_CODE_ID   = "cnt-user_code";
    public static final String CURRENT_UNIT_CODE   = "cnt-current-unit-code";
    public static final String AUTHORIZATION_TOKEN  = "Authorization";
    public static final String SESSION_ID_TOKEN     = "x-auth-token";

    private String correlationId;
    private String sessionIdToken;
    private String userCode;
    private String currUnitCode;
    private String authorizationToken;
}
