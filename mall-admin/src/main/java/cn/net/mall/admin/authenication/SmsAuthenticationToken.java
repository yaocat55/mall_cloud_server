package cn.net.mall.admin.authenication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 手机号登录验证 token
 *
 * @date 2024/11/8 下午4:10
 */
public class SmsAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credentials;

    public SmsAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal; // 用户的手机号
        this.credentials = credentials; // 验证码
        setAuthenticated(false);
    }

    public SmsAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}
