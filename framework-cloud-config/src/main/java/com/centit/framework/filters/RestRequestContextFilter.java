package com.centit.framework.filters;

import com.centit.framework.utils.RestRequestContext;
import com.centit.framework.utils.RestRequestContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class RestRequestContextFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RestRequestContextFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        RestRequestContext restRequestContext = RestRequestContextHolder.getContext();
        restRequestContext.setCorrelationId(httpServletRequest.getHeader(RestRequestContext.CORRELATION_ID) );
        restRequestContext.setUserCode(httpServletRequest.getHeader(RestRequestContext.USER_CODE_ID));
        restRequestContext.setSessionIdToken(httpServletRequest.getHeader(RestRequestContext.SESSION_ID_TOKEN));
        restRequestContext.setCurrUnitCode(httpServletRequest.getHeader(RestRequestContext.CURRENT_UNIT_CODE));
        restRequestContext.setAuthorizationToken(httpServletRequest.getHeader(RestRequestContext.AUTHORIZATION_TOKEN));

        logger.debug("Special Routes Service Incoming Correlation id: {}", restRequestContext.getCorrelationId());

        filterChain.doFilter(httpServletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
