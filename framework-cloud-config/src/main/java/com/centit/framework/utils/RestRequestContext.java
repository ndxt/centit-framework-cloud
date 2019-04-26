package com.centit.framework.utils;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RestRequestContext {
    private String correlationId;
    private String sessionIdToken;
    private String authorizationToken;
    private String userCode;
    private String currUnitCode;
    private String currStationId;
}
