package com.loadburn.heron.captcha.config;

import com.loadburn.heron.captcha.config.background.GuiceWordGen;
import com.loadburn.heron.captcha.config.background.GuiceWordToImage;
import com.octo.captcha.image.gimpy.GimpyFactory;

import java.io.Serializable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-19
 */
public class GuiceCaptchaFactory implements Serializable {

    private static final long serialVersionUID = 8264498043510798478L;
    private GimpyFactory captchaFactory;
    private String className;
    private GuiceWordGen wordGen;
    private GuiceWordToImage wordToImage;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public GuiceWordGen getWordGen() {
        return wordGen;
    }

    public void setWordGen(GuiceWordGen wordGen) {
        this.wordGen = wordGen;
    }

    public GuiceWordToImage getWordToImage() {
        return wordToImage;
    }

    public void setWordToImage(GuiceWordToImage wordToImage) {
        this.wordToImage = wordToImage;
    }

    public GimpyFactory getCaptchaFactory() {
        captchaFactory = new GimpyFactory(wordGen.getWordGen(), wordToImage.getWordToImage());
        return captchaFactory;
    }

}
