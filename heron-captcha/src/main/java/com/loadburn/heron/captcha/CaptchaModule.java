package com.loadburn.heron.captcha;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.loadburn.heron.captcha.bind.CaptchaConfig;
import com.loadburn.heron.captcha.bind.CaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-18
 */
public class CaptchaModule extends AbstractModule {

    private final String captchaConfigFile;

    public CaptchaModule(String captchaConfigFile) {
        this.captchaConfigFile = captchaConfigFile;
    }

    public CaptchaModule() {
        captchaConfigFile = "captcha.json";
    }

    @Override
    protected void configure() {

        bind(CaptchaService.class).in(Singleton.class);

        bindConstant().annotatedWith(CaptchaConfig.class).to(captchaConfigFile);
        bind(ImageCaptchaService.class)
                .toProvider(CaptchaService.CaptchaServiceProvider.class);


    }

}
