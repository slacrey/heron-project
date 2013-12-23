package com.loadburn.heron.captcha.bind;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.loadburn.heron.captcha.config.GuiceCaptchaService;
import com.loadburn.heron.exceptions.ConvertException;
import com.loadburn.heron.utils.ConfigLoader;
import com.octo.captcha.service.image.ImageCaptchaService;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-19
 */
@Singleton
public class CaptchaService implements Serializable {

    private final static String CONFIG_FILE = "captcha.json";
    private static final long serialVersionUID = 6229843297636248975L;
    private final String persistenceConfigFile;
    private final Provider<ServletContext> contextProvider;
    private ConfigLoader configLoader;
    private GuiceCaptchaService guiceCaptchaService;

    private void initCaptcha() {
        configLoader = ConfigLoader.newInstance(contextProvider.get());
        String json;
        if (persistenceConfigFile != null) {
            json = configLoader.loadConfig(persistenceConfigFile);
        } else {
            json = configLoader.loadConfig(CONFIG_FILE);
        }

        try {
            guiceCaptchaService = configLoader.convertJsonToTObject(json, GuiceCaptchaService.class);
        } catch (IOException e) {
            throw new ConvertException("JSON转换为指定对象出错", e);
        }
    }

    @Inject
    public CaptchaService(@CaptchaConfig String persistenceConfigFile, Provider<ServletContext> contextProvider) {
        this.persistenceConfigFile = persistenceConfigFile;
        this.contextProvider = contextProvider;

        initCaptcha();

    }

    public GuiceCaptchaService getGuiceCaptchaService() {
        return guiceCaptchaService;
    }

    @Singleton
    public static class CaptchaServiceProvider implements Provider<ImageCaptchaService> {

        private final CaptchaService captchaService;

        @Inject
        public CaptchaServiceProvider(CaptchaService captchaService) {
            this.captchaService = captchaService;
        }

        @Override
        public ImageCaptchaService get() {
            return captchaService.getGuiceCaptchaService().getCaptchaService();
        }

    }

}
