package com.loadburn.heron.captcha.config.font;

import com.google.common.collect.Lists;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;

import java.awt.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-19
 */
public class GuiceFontGen implements Serializable {

    private static final long serialVersionUID = -5102657997823869959L;
    private RandomFontGenerator fontGen;
    private String className;
    private GuiceFont[] fonts;
    private int minFontSize;
    private int maxFontSize;

    public RandomFontGenerator getFontGen() {
        List<Font> fontList = Lists.newArrayList();
        for (GuiceFont guiceFont: fonts) {
            fontList.add(guiceFont.getFont());
        }
        Font[] fontArray = new Font[fontList.size()];
        fontGen = new RandomFontGenerator(minFontSize, maxFontSize, fontList.toArray(fontArray));
        return fontGen;
    }

    public void setFontGen(RandomFontGenerator fontGen) {
        this.fontGen = fontGen;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public GuiceFont[] getFonts() {
        return fonts;
    }

    public void setFonts(GuiceFont[] fonts) {
        this.fonts = fonts;
    }

    public int getMinFontSize() {
        return minFontSize;
    }

    public void setMinFontSize(int minFontSize) {
        this.minFontSize = minFontSize;
    }

    public int getMaxFontSize() {
        return maxFontSize;
    }

    public void setMaxFontSize(int maxFontSize) {
        this.maxFontSize = maxFontSize;
    }
}
