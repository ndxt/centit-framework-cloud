package com.centit.framework.config.demo;

import com.centit.framework.common.WebOptUtils;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.TopUnitSecurityMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

/**
 * @author zfg
 */
@RefreshScope
@RequestMapping("/test")
@RestController
public class NacosConfigTest {

    @Autowired
    private DiscoveryClient client;

    @Value("${name}")
    private String name;

    @Value("${server.port}")
    private String port;

    @GetMapping("/get")
    public String getStr(HttpServletRequest request) {
        //测试metadata.matchUrlToRole
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        TopUnitSecurityMetadata metadata = CentitSecurityMetadata.securityMetadata.getCachedValue(topUnit);
        List<ConfigAttribute> needRoles2 = metadata.matchUrlToRole("system/mainframe/logincas", request);
        System.out.println(needRoles2);
        List<ConfigAttribute> needRoles = metadata.matchUrlToRole(request.getRequestURI(), request);
        System.out.println(needRoles);
        return name + ":" + port;
    }

    @GetMapping("/discovery")
    public List<String> discoveryHandle() {
        // 获取到注册中心的所有微服务名称
        List<String> services = client.getServices();
        // 遍历所有微服务名称
        for (String serviceName : services) {
            // 获取指定服务名称的所有提供者实例
            List<ServiceInstance> instances = client.getInstances(serviceName);
            for (ServiceInstance instance : instances) {
                String serviceId = instance.getServiceId();
                URI uri = instance.getUri();
                String host = instance.getHost();
                int port = instance.getPort();

                System.out.println("serviceId = " + serviceId);
                System.out.println("uri = " + uri);
                System.out.println("host = " + host);
                System.out.println("port = " + port);
            }
        }

        return services;
    }

}
