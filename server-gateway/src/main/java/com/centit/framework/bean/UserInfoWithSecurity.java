package com.centit.framework.bean;

import com.centit.framework.system.po.UserInfo;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息类，同时封装了SpringSecurity所需要的相关信息
 */
@Data
public class UserInfoWithSecurity implements UserDetails {
    //用户信息bean来自公共代码包commons_code
    private UserInfo userInfo;

//    //security存储权限认证用的
    @Transient
    private List<GrantedAuthority> authorities = new ArrayList<>();

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return userInfo.getUserPwd();
    }

    @Override
    public String getUsername() {
        return userInfo.getUserName();
    }

    /**
     * 账号是否未过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账号是否未被锁定
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 用户账号凭证(密码)是否未过期
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否被启用
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
