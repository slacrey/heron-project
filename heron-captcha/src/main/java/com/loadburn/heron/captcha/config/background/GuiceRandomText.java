package com.loadburn.heron.captcha.config.background;

import com.loadburn.heron.captcha.config.color.RangeColorGen;
import com.octo.captcha.component.image.textpaster.RandomTextPaster;

import java.io.Serializable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-19
 */
public class GuiceRandomText implements Serializable {

    private static final long serialVersionUID = -8557410136434726768L;
    private String className;
    private RandomTextPaster randomText;
    private int minAcceptedWordLength;
    private int maxAcceptedWordLength;
    private RangeColorGen rangeColorGen;
    private boolean manageColorPerGlyph = false;

    public boolean isManageColorPerGlyph() {
        return manageColorPerGlyph;
    }

    public void setManageColorPerGlyph(boolean manageColorPerGlyph) {
        this.manageColorPerGlyph = manageColorPerGlyph;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public RandomTextPaster getRandomText() {
        randomText = new RandomTextPaster(minAcceptedWordLength, maxAcceptedWordLength, rangeColorGen.getRangeColorGen());
        return randomText;
    }

    public int getMinAcceptedWordLength() {
        return minAcceptedWordLength;
    }

    public void setMinAcceptedWordLength(int minAcceptedWordLength) {
        this.minAcceptedWordLength = minAcceptedWordLength;
    }

    public int getMaxAcceptedWordLength() {
        return maxAcceptedWordLength;
    }

    public void setMaxAcceptedWordLength(int maxAcceptedWordLength) {
        this.maxAcceptedWordLength = maxAcceptedWordLength;
    }

    public RangeColorGen getRangeColorGen() {
        return rangeColorGen;
    }

    public void setRangeColorGen(RangeColorGen rangeColorGen) {
        this.rangeColorGen = rangeColorGen;
    }
}
