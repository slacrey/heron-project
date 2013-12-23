package com.loadburn.heron.captcha.config;

import com.octo.captcha.service.multitype.GenericManageableCaptchaService;

import java.io.Serializable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-19
 */
public class GuiceCaptchaService implements Serializable {

    private static final long serialVersionUID = -3049861501720881676L;
    private GenericManageableCaptchaService captchaService;
    private String className;
    private GuiceImageEngine imageEngine;
    private int timeOut = 60;  //超时时间秒
    private int concurrentCount = 10000;  //并发数

    public GenericManageableCaptchaService getCaptchaService() {
        captchaService = new GenericManageableCaptchaService(imageEngine.getImageEngine(), timeOut, concurrentCount);
        return captchaService;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public GuiceImageEngine getImageEngine() {
        return imageEngine;
    }

    public void setImageEngine(GuiceImageEngine imageEngine) {
        this.imageEngine = imageEngine;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public int getConcurrentCount() {
        return concurrentCount;
    }

    public void setConcurrentCount(int concurrentCount) {
        this.concurrentCount = concurrentCount;
    }
}
