package com.centit.framework.oauth.config;

/*import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitUserDetailsService;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import com.centit.framework.system.security.DaoUserDetailsService;
import com.centit.framework.system.service.impl.DBPlatformEnvironment;
import com.centit.support.algorithm.BooleanBaseOpt;*/

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * SpringSecurity配置
 *
 * @author
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //@Autowired
    //private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*http.authorizeRequests()
            .antMatchers("/rsa/publicKey","/login", "/oauth/authorize")
            .permitAll()
            .anyRequest().authenticated()
            .and().formLogin().loginPage("/login");*/
        http.authorizeRequests()
            .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
            .antMatchers("/rsa/publicKey").permitAll()
            .anyRequest().authenticated()
            .and().formLogin().permitAll();
    }

    /**
     * 配置用户账户的认证方式，显然，我们把用户存在了数据库中希望配置JDBC的方式。
     * 此外，我们还配置了使用BCryptPasswordEncoder哈希来保存用户的密码（生产环境的用户密码肯定不能是明文保存）
     * @param auth
     * @throws Exception
     */
    /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
            .dataSource(dataSource)
            .passwordEncoder(passwordEncoder());
    }*/

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*@Bean("passwordEncoder")
    public StandardPasswordEncoderImpl passwordEncoder() {
        return new StandardPasswordEncoderImpl();
    }*/

    /*@Bean
    public PlatformEnvironment platformEnvironment() {
        boolean tenant = BooleanBaseOpt.castObjectToBoolean(true, false);
        DBPlatformEnvironment platformEnvironment = new DBPlatformEnvironment();
        platformEnvironment.setSupportTenant(tenant);
        return platformEnvironment;
    }

    @Bean
    public CentitUserDetailsService centitUserDetailsService() {
        DaoUserDetailsService userDetailsService = new DaoUserDetailsService();
        return userDetailsService;
    }*/

}
