package com.centit.framework.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class TrackingFilter extends ZuulFilter {
    private static final int      FILTER_ORDER = 10;
    private static final boolean  SHOULD_FILTER=true;
    private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return null;
        }
        String sessionId = request.getHeader("x-auth-token");
        if(StringUtils.isBlank(sessionId)){
            sessionId = request.getSession().getId();
        }
        ctx.addZuulRequestHeader("x-auth-token", sessionId);
/*
        String correlationId = request.getHeader(WebOptUtils.CORRELATION_ID);
        if (StringUtils.isBlank(correlationId)) {
            correlationId = ctx.getZuulRequestHeaders().get(WebOptUtils.CORRELATION_ID);
        }

        if (StringUtils.isBlank(correlationId)) {
            correlationId = UUID.randomUUID().toString();
            ctx.addZuulRequestHeader(WebOptUtils.CORRELATION_ID, correlationId);
        }

        ctx.addZuulRequestHeader(WebOptUtils.CORRELATION_ID, correlationId);
        String sessionId = request.getHeader(WebOptUtils.SESSION_ID_TOKEN);
        if(StringUtils.isBlank(sessionId)){
            sessionId = request.getSession().getId();
        }
        ctx.addZuulRequestHeader(WebOptUtils.SESSION_ID_TOKEN, sessionId);
        ctx.addZuulRequestHeader(WebOptUtils.AUTHORIZATION_TOKEN, request.getHeader(WebOptUtils.AUTHORIZATION_TOKEN));
        //  如何确保 zuul 过滤器在 spring session 的过滤器之后
        Object ud = WebOptUtils.getLoginUser(request);
        if(ud instanceof CentitUserDetails){
            CentitUserDetails userDetails = (CentitUserDetails)ud;
            ctx.addZuulRequestHeader(WebOptUtils.CURRENT_USER_CODE_TAG, userDetails.getUserCode());
            ctx.addZuulRequestHeader(WebOptUtils.CURRENT_UNIT_CODE_TAG, userDetails.getCurrentUnitCode());
            ctx.addZuulRequestHeader(WebOptUtils.CURRENT_STATION_ID_TAG, userDetails.getCurrentStation().getString("userUnitId"));
        }*/
        return null;
    }
}
