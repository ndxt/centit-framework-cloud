package com.centit.framework.utils;

import com.centit.framework.common.WebOptUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestRequestContextInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(
        HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        RestRequestContext restRequestContext = RestRequestContextHolder.getContext();
        HttpHeaders headers = request.getHeaders();
        headers.add(WebOptUtils.CORRELATION_ID, restRequestContext.getCorrelationId());
        headers.add(WebOptUtils.SESSION_ID_TOKEN,restRequestContext.getSessionIdToken());
        headers.add(WebOptUtils.AUTHORIZATION_TOKEN,restRequestContext.getAuthorizationToken());
        headers.add(WebOptUtils.CURRENT_USER_CODE_TAG,restRequestContext.getUserCode());
        headers.add(WebOptUtils.CURRENT_UNIT_CODE_TAG, restRequestContext.getCurrUnitCode());
        headers.add(WebOptUtils.CURRENT_STATION_ID_TAG, restRequestContext.getCurrStationId());
        return execution.execute(request, body);
    }
}
