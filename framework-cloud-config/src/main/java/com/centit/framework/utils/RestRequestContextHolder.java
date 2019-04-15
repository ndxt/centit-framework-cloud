package com.centit.framework.utils;


import org.springframework.util.Assert;

public class RestRequestContextHolder {
    private static final ThreadLocal<RestRequestContext> userContext = new ThreadLocal<RestRequestContext>();

    public static final RestRequestContext getContext() {
        RestRequestContext context = userContext.get();
        if (context == null) {
            context = createEmptyContext();
            userContext.set(context);

        }
        return userContext.get();
    }

    public static final void setContext(RestRequestContext context) {
        Assert.notNull(context, "Only non-null UserContext instances are permitted");
        userContext.set(context);
    }

    public static final RestRequestContext createEmptyContext(){
        return new RestRequestContext();
    }
}
