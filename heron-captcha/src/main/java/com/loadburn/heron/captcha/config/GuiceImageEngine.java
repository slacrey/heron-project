package com.loadburn.heron.captcha.config;

import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.engine.GenericCaptchaEngine;

import java.io.Serializable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-19
 */
public class GuiceImageEngine implements Serializable {

    private static final long serialVersionUID = -8793922877866507859L;
    private String className;
    private GenericCaptchaEngine imageEngine;
    private GuiceCaptchaFactory captchaFactory;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public GenericCaptchaEngine getImageEngine() {
        imageEngine = new GenericCaptchaEngine(new CaptchaFactory[]{captchaFactory.getCaptchaFactory()});
        return imageEngine;
    }

    public GuiceCaptchaFactory getCaptchaFactory() {
        return captchaFactory;
    }

    public void setCaptchaFactory(GuiceCaptchaFactory captchaFactory) {
        this.captchaFactory = captchaFactory;
    }
}
