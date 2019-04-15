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
        headers.add(RestRequestContext.CORRELATION_ID, RestRequestContextHolder.getContext().getCorrelationId());
        headers.add(RestRequestContext.AUTH_TOKEN, RestRequestContextHolder.getContext().getAuthToken());
        headers.add(RestRequestContext.USER_CODE_ID, RestRequestContextHolder.getContext().getUserCode());
        headers.add(RestRequestContext.CURRENT_UNIT_CODE, RestRequestContextHolder.getContext().getCurrUnitCode());
        return execution.execute(request, body);
    }
}
