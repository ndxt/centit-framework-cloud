package com.centit.framework.security;

import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import java.io.IOException;

public class CloudFilterSecurityInterceptor extends AbstractSecurityInterceptor
        implements Filter {

    private static String AUTHORIZE_SERVICE_URL="http://AUTHORIZE-SERVICE";
    private static CentitUserDetails anonymousUser = AnonymousUserDetails.createAnonymousUser();
    private RestTemplate restTemplate;

    private FilterInvocationSecurityMetadataSource securityMetadataSource;

    // ~ Methods
    // ========================================================================================================
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Method that is actually called by the filter chain. Simply delegates to
     * the {@link #invoke(FilterInvocation)} method.
     *
     * @param request  the servlet request
     * @param response the servlet response
     * @param chain    the filter chain
     * @throws IOException      if the filter chain fails
     * @throws ServletException if the filter chain fails
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);
        invoke(fi);

        // throw new AccessDeniedException("");
    }

    public FilterInvocationSecurityMetadataSource getSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    public Class<? extends Object> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    public void invoke(FilterInvocation fi) throws IOException,
            ServletException {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        boolean alwaysReauthenticate = false;


        //从token中获取用户信息
        if(authentication==null || "anonymousUser".equals(authentication.getName())){

            String accessToken = fi.getHttpRequest().getParameter(SecurityContextUtils.SecurityContextTokenName);
            if(StringUtils.isBlank(accessToken)) {
                accessToken = fi.getHttpRequest().getHeader("Authorization");
            }
            CentitUserDetails ud = null;
            if(StringUtils.isNotBlank(accessToken)) {
                try {
                    String jsonString =
                            restTemplate.getForObject(AUTHORIZE_SERVICE_URL + "/oauthUser/",
                                    String.class);
                    HttpReceiveJSON responseJSON = HttpReceiveJSON.valueOfJson(jsonString);
                    ud = responseJSON.getDataAsObject(JsonCentitUserDetails.class);
                } catch (Exception e) {
                    ud = null;
                }

                if(ud!=null){
                    alwaysReauthenticate = this.isAlwaysReauthenticate();
                    if(alwaysReauthenticate)
                        this.setAlwaysReauthenticate(false);
                    SecurityContextHolder.getContext().setAuthentication(ud);
                }else if(authentication==null){
                    alwaysReauthenticate = this.isAlwaysReauthenticate();
                    if(alwaysReauthenticate)
                        this.setAlwaysReauthenticate(false);
                    SecurityContextHolder.getContext().setAuthentication(anonymousUser);
                }
            }
        }

        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }

        if(alwaysReauthenticate)
            this.setAlwaysReauthenticate(true);
    }

    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    public void setSecurityMetadataSource(
            FilterInvocationSecurityMetadataSource newSource) {
        this.securityMetadataSource = newSource;
    }

    public void destroy() {
    }

    public void init(FilterConfig arg0) throws ServletException {
    }

}

