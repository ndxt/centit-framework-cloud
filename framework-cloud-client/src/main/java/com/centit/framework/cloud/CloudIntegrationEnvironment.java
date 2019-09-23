package com.centit.framework.cloud;

import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.ip.po.DatabaseInfo;
import com.centit.framework.ip.po.OsInfo;
import com.centit.framework.ip.po.UserAccessToken;
import com.centit.framework.ip.service.impl.AbstractIntegrationEnvironment;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by codefan on 17-7-3.
 */
@Service
public class CloudIntegrationEnvironment extends AbstractIntegrationEnvironment {

    public CloudIntegrationEnvironment() {
        super();
    }

    @Autowired
    RestTemplate restTemplate;

    protected String FRAMEWORK_SERVER_URL;

    @Value("${serives.framework.url:http://FRAMEWORK-SERVICE}")
    public void setFrameworkUrl(String frameworkUrl){
        FRAMEWORK_SERVER_URL = frameworkUrl +"/platform";
    }

    @Override
    @HystrixCommand(fallbackMethod = "dummyReloadOsInfos")
    public List<OsInfo> reloadOsInfos() {

        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
            restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/ipenvironment/osinfo",
                String.class));
        return receiveJSON.getDataAsArray(OsInfo.class);
    }

    public List<OsInfo>  dummyReloadOsInfos(){
        return null;
    }

    @Override
    @HystrixCommand(fallbackMethod = "dummyReloadDatabaseInfos")
    public List<DatabaseInfo> reloadDatabaseInfos() {
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
            restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/ipenvironment/databaseinfo",
                String.class));
        return receiveJSON.getDataAsArray(DatabaseInfo.class);
    }

    public List<DatabaseInfo>  dummyReloadDatabaseInfos(){
        return null;
    }

    @Override
    @HystrixCommand(fallbackMethod = "dummyReloadAccessTokens")
    public List<UserAccessToken> reloadAccessTokens() {
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
            restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/ipenvironment/allUserToken",
                String.class));
        return receiveJSON.getDataAsArray(UserAccessToken.class);
    }

    public List<UserAccessToken>  dummyReloadAccessTokens(){
        return null;
    }

    @Override
    @HystrixCommand(fallbackMethod = "dummyCheckAccessToken")
    public String checkAccessToken(String tokenId, String accessKey) {

        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
            restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/ipenvironment/userToken/"+tokenId,
                String.class));
        UserAccessToken at = receiveJSON.getDataAsObject(UserAccessToken.class);

        if(at==null)
            return null;
        if(StringUtils.equals(at.getTokenId(),tokenId)){
            if( StringUtils.equals(at.getIsValid(),"T")
                    && StringUtils.equals(at.getSecretAccessKey(), accessKey) )
                return at.getUserCode();
            else
                return null;
        }
        return null;
    }

    public String dummyCheckAccessToken(String tokenId, String accessKey) {
        return null;
    }

}
