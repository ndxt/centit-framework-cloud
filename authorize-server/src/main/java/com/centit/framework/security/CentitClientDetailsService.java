package com.centit.framework.security;

import com.centit.framework.ip.dao.OsInfoDao;
import com.centit.framework.ip.po.OsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

@Service
public class CentitClientDetailsService implements ClientDetailsService {

    @Autowired
    public OsInfoDao osInfoDao;


    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        OsInfo osInfo = osInfoDao.getObjectById(clientId);
        if(osInfo==null) {
            return null;
        }
        return new CentitClientDetails(osInfo);
    }
}
