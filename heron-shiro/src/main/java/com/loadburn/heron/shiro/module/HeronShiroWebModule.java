package com.loadburn.heron.shiro.module;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;
import com.loadburn.heron.shiro.security.CaptchaFormAuthenticationFilter;
import com.loadburn.heron.shiro.security.SystemAuthorizingRealm;
import com.octo.captcha.service.image.ImageCaptchaService;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import javax.servlet.ServletContext;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-18
 */
public class HeronShiroWebModule extends ShiroWebModule {

    public HeronShiroWebModule(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    protected void configureShiroWeb() {

        Key customFilter = Key.get(CaptchaFormAuthenticationFilter.class);

        addFilterChain("/custom/**", customFilter);

        bindRealm().to(SystemAuthorizingRealm.class);
        bind(CredentialsMatcher.class).to(HashedCredentialsMatcher.class);
        bind(HashedCredentialsMatcher.class);
        bindConstant().annotatedWith(Names.named("shiro.hashAlgorithmName")).to(Md5Hash.ALGORITHM_NAME);
        bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
        addFilterChain("/public/**", ANON);
    }

    @Override
    protected void bindWebSecurityManager(AnnotatedBindingBuilder<? super WebSecurityManager> bind) {
        super.bindWebSecurityManager(bind);
    }

    @Override
    protected void bindSessionManager(AnnotatedBindingBuilder<SessionManager> bind) {
        bind.to(DefaultWebSessionManager.class);
        bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(5000L);
        bind(DefaultWebSessionManager.class);
        bind(Cookie.class).toInstance(new SimpleCookie("heronCookie"));
    }

    @Override
    protected void bindWebEnvironment(AnnotatedBindingBuilder<? super WebEnvironment> bind) {
        super.bindWebEnvironment(bind);
    }

    @Provides
    ImageCaptchaService getImageCaptchaService() {
        return null;
    }
}
