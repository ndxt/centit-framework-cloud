package com.centit.framework.utils;

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
        headers.add(RestRequestContext.CORRELATION_ID, restRequestContext.getCorrelationId());
        headers.add(RestRequestContext.SESSION_ID_TOKEN,restRequestContext.getSessionIdToken());
        headers.add(RestRequestContext.AUTHORIZATION_TOKEN,restRequestContext.getAuthorizationToken());
        headers.add(RestRequestContext.USER_CODE_ID,restRequestContext.getUserCode());
        headers.add(RestRequestContext.CURRENT_UNIT_CODE, restRequestContext.getCurrUnitCode());
        return execution.execute(request, body);
    }
}
