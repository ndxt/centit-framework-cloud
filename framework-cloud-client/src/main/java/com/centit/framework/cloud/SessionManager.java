package com.centit.framework.cloud;

import com.centit.framework.security.model.CentitUserDetails;

public interface SessionManager {
    CentitUserDetails getUserByToken(String userToken);
}
