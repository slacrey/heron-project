package com.loadburn.heron.captcha.config.background;

import com.loadburn.heron.captcha.config.font.GuiceFontGen;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;

import java.io.Serializable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-19
 */
public class GuiceWordToImage implements Serializable {

    private static final long serialVersionUID = -7724906768221345733L;
    private ComposedWordToImage wordToImage;
    private String className;
    private GuiceFontGen fontGenRandom;
    private GuiceBackGroundGen backgroundGen;
    private GuiceRandomText randomText;

    public ComposedWordToImage getWordToImage() {
        wordToImage = new ComposedWordToImage(fontGenRandom.getFontGen(), backgroundGen.getBackGroundGen(),
                randomText.getRandomText());
        return wordToImage;
    }

    public void setWordToImage(ComposedWordToImage wordToImage) {
        this.wordToImage = wordToImage;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public GuiceFontGen getFontGenRandom() {
        return fontGenRandom;
    }

    public void setFontGenRandom(GuiceFontGen fontGenRandom) {
        this.fontGenRandom = fontGenRandom;
    }

    public GuiceBackGroundGen getBackgroundGen() {
        return backgroundGen;
    }

    public void setBackgroundGen(GuiceBackGroundGen backgroundGen) {
        this.backgroundGen = backgroundGen;
    }

    public GuiceRandomText getRandomText() {
        return randomText;
    }

    public void setRandomText(GuiceRandomText randomText) {
        this.randomText = randomText;
    }
}
