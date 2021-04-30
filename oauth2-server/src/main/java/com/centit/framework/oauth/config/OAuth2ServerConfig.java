package com.centit.framework.oauth.config;

import com.centit.framework.oauth.component.JwtTokenEnhancer;
import com.centit.framework.oauth.service.ClientService;
import com.centit.framework.oauth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

//import com.centit.framework.security.model.CentitUserDetailsService;

/**
 * 认证服务器配置
 *
 * @author
 */
@AllArgsConstructor
@Configuration
@EnableAuthorizationServer
public class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    private final UserService userService;

    //@Autowired
    //private CentitUserDetailsService userDetailsService;
    //private final ClientService clientService;
    @Autowired
    private ClientService clientService;

    //private final AuthenticationManager authenticationManager;
    @Autowired
    private AuthenticationManager authenticationManager;

    //private final JwtTokenEnhancer jwtTokenEnhancer;
    @Autowired
    private JwtTokenEnhancer jwtTokenEnhancer;

    /**
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientService);
        //TODO 使用数据库来维护客户端信息 后期建立一套工作流来允许客户端自己申请ClientID
        //clients.jdbc(dataSource);
    }

    /**
     * 这里干了两个事情，首先打开了验证Token的访问权限
     * 然后允许ClientSecret明文方式保存并且可以通过表单提交（而不仅仅是Basic Auth方式提交）
     * @param security
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients();
        //security.checkTokenAccess("permitAll()").allowFormAuthenticationForClients().passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    /**
     *
     * @param endpoints
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(jwtTokenEnhancer);
        delegates.add(accessTokenConverter());
        enhancerChain.setTokenEnhancers(delegates); //配置JWT的内容增强器

        endpoints
            .userDetailsService(userService) //配置加载用户信息的服务
            .accessTokenConverter(accessTokenConverter())
            //.tokenStore(tokenStore())
            .tokenEnhancer(enhancerChain)
            .authenticationManager(authenticationManager);
    }

    /**
     * 使用JWT令牌存储
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    @Bean
    public KeyPair keyPair() {
        // 从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "654321".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "654321".toCharArray());
    }

}
