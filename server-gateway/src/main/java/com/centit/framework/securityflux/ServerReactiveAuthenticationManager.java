package com.centit.framework.securityflux;

import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.framework.security.model.JsonCentitUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

/**
 * 自定义的登录验证器<br>
 * 由于SpringGateWay基于WebFlux，所以SpringSecruity很多原有写法，都得改为WebFlux的方式才能生效！
 */
//此类会自动注入到SpringSecurity中，无需手动指定配置
@Configuration
public class ServerReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    //解密用的（在SpringSecurityConfig中配置的bean）
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CentitUserDetailsService userDetailsService;

    @Autowired
    private PlatformEnvironment platformEnvironment;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        //获取输入的用户名
        String username = authentication.getName();
        //获取输入的明文
        String rawPassword = (String) authentication.getCredentials();


        JsonCentitUserDetails user = null;
        try {
            user = (JsonCentitUserDetails) userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException ufe) {
            return Mono.error(ufe);
        }

        //用户若不存在，则会在上面方法中抛出异常，所以能到这里用户一定存在

        if (!user.isEnabled()) {
            return Mono.error(new DisabledException("该账户已被禁用，请联系管理员"));

        } else if (!user.isAccountNonLocked()) {
            return Mono.error(new LockedException("该账号已被锁定"));
        } else if (!user.isAccountNonExpired()) {
            return Mono.error(new AccountExpiredException("该账号已过期，请联系管理员"));

        } else if (!user.isCredentialsNonExpired()) {
            return Mono.error(new CredentialsExpiredException("该账户的登录凭证已过期，请重新登录"));
        }

        //验证密码
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return Mono.error(new BadCredentialsException("输入密码错误!"));
        }

        Authentication authentication1 = new UsernamePasswordAuthenticationToken(user, rawPassword, user.getAuthorities());

        //WebFlux方式默认没有放到context中，需要手动放入
        //本地测试时不设置strategyName->SecurityContextHolder.MODE_GLOBAL,其他请求获取不到SecurityContextHolder.getContext()
        //但session中可以获取到认证信息
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        SecurityContextHolder.getContext().setAuthentication(authentication1);
        //ReactiveSecurityContextHolder
        return Mono.just(authentication1);
    }
}
