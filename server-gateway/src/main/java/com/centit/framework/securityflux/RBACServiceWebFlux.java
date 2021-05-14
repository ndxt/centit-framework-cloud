package com.centit.framework.securityflux;

import com.centit.framework.security.DaoInvocationSecurityMetadataSource;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.security.model.TopUnitSecurityMetadata;
import com.centit.framework.util.RedisService;
import com.centit.framework.util.RequestUrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * 自定义的鉴权服务，通过鉴权的才能继续访问某个请求<br>
 * 由于SpringGateWay基于WebFlux，所以SpringSecruity很多原有写法，都得改为WebFlux的方式才能生效！
 */
@Component
public class RBACServiceWebFlux implements ReactiveAuthorizationManager<AuthorizationContext> {

    private static final Logger logger = LoggerFactory.getLogger(RBACServiceWebFlux.class);

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private DaoInvocationSecurityMetadataSource daoInvocationSecurityMetadataSource;

    //@Autowired
    //private RedisService redisService;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> monoauthentication, AuthorizationContext object) {
        ServerHttpRequest request = object.getExchange().getRequest();
        String uri = request.getPath().pathWithinApplication().value();
        if (RequestUrlUtils.ignoreUrl(request.getURI())) {
            return Mono.just(new AuthorizationDecision(true));
        }

        Map<String, CentitUserDetails> userDetailsHashMap = new HashMap<>();
        object.getExchange().getSession().flatMap(
            webSession -> {
                //CentitUserDetails details = (JsonCentitUserDetails) redisService.get(webSession.getId());
                CentitUserDetails details = webSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                if (null != details) {
                    userDetailsHashMap.put("details", details);
                }
                return Mono.just(webSession);
            }
        ).subscribe();

        CentitUserDetails userDetails = userDetailsHashMap.get("details");
        //TODO 认证后需将url映射到业务操作，并查找对应的角色集合，并判断用户是否有权限访问资源
        //http://localhost:10088/system/mainframe/logincas 模拟鉴权调用
        if (userDetails == null) {
            return Mono.just(new AuthorizationDecision(false));
        }
        //CentitUserDetails userDetails = (JsonCentitUserDetails) authentication;
        String topUnitCode = userDetails.getTopUnitCode();
        if (topUnitCode == null) {
            topUnitCode = "";
        }
        Collection<? extends GrantedAuthority> userRoles = userDetails.getAuthorities();
        //待优化调整
        TopUnitSecurityMetadata metadata = CentitSecurityMetadata.securityMetadata.getCachedValue(topUnitCode);
        FilterInvocation filterInvocation = new FilterInvocation(uri, request.getMethod().name());
        List<ConfigAttribute> configAttributes = metadata.matchUrlToRole(uri, filterInvocation.getHttpRequest());
        boolean isAccess = false;
        if (configAttributes == null) {
            isAccess = true;
        }
        if (userRoles != null && configAttributes != null) {
            Iterator<? extends GrantedAuthority> userRolesItr = userRoles.iterator();
            Iterator<ConfigAttribute> needRolesItr = configAttributes.iterator();

            //将两个集合排序 是可以提高效率的， 但考虑到这两个集合都比较小（一般应该不会大于3）所以优化的意义不大
            String needRole = needRolesItr.next().getAttribute();
            String userRole = userRolesItr.next().getAuthority();
            while (true) {
                int n = needRole.compareTo(userRole);
                if (n == 0) {
                    isAccess = true;
                    break; // 匹配成功
                }

                if (n < 0) {
                    if (!needRolesItr.hasNext())
                        break;
                    needRole = needRolesItr.next().getAttribute();
                } else {
                    if (!userRolesItr.hasNext())
                        break;
                    userRole = userRolesItr.next().getAuthority();
                }
            }
        }
        if ("anonymousUser".equals(userDetails.getUserCode())) {
            isAccess = false;
        }
        return Mono.just(new AuthorizationDecision(isAccess));
    }

    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        return null;
    }
}
