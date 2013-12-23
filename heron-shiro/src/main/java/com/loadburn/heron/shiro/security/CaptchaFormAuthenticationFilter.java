package com.loadburn.heron.shiro.security;

import com.loadburn.heron.shiro.ShiroConstants;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-18
 */
public class CaptchaFormAuthenticationFilter extends FormAuthenticationFilter {

    private String captchaParam = ShiroConstants.CAPTCHA_KEY;

    public String getCaptchaParam() {
        return captchaParam;
    }

    protected String getCaptcha(ServletRequest request) {
        return WebUtils.getCleanParam(request, getCaptchaParam());
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request,
                                              ServletResponse response) {
        String username = getUsername(request);
        String password = getPassword(request);
        String captcha = getCaptcha(request);
        boolean rememberMe = isRememberMe(request);
        String host = getHost(request);
        return new CaptchaUsernamePasswordToken(username, password, rememberMe,
                host, captcha);
    }

}
