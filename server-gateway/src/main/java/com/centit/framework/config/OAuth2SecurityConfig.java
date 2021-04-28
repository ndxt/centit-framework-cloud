package com.centit.framework.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * 资源服务器配置
 */

@Configuration
//@EnableWebFluxSecurity
@EnableOAuth2Client
@EnableWebSecurity
public class OAuth2SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    OAuth2ClientAuthenticationProcessingFilter oauthClientAuthProcessingFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
            .authorizeRequests()
            .antMatchers("/","login**")
            .permitAll().anyRequest().authenticated()
            .and()
            .addFilterBefore(oauthClientAuthProcessingFilter, BasicAuthenticationFilter.class)
            .csrf().disable();
    /*oauth2Login()
                .redirectionEndpoint()
                    .baseUri("/custom-callback");*/
    }
}
