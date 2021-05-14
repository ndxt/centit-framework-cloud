package com.centit.framework.util;

import java.net.URI;

/**
 * @author zfg
 */
public class RequestUrlUtils {

    public static final boolean ignoreUrl(URI uri) {
        String szUrl = uri.toString();
        if (szUrl.endsWith("css") || szUrl.endsWith("js") || szUrl.endsWith("map") ||
            szUrl.endsWith("jpg") || szUrl.endsWith("png") || szUrl.endsWith(".ico") ||
            szUrl.equals("gif")) {
            return true;
        }
        return false;
    }

}
