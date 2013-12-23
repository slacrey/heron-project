package com.loadburn.heron.captcha.config.color;

import com.octo.captcha.component.image.color.RandomRangeColorGenerator;

import java.io.Serializable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-19
 */
public class RangeColorGen implements Serializable {

    private static final long serialVersionUID = 3012342599812330706L;
    private String className;
    private RandomRangeColorGenerator rangeColorGen;
    private int[] redComponentRange;
    private int[] greenComponentRange;
    private int[] blueComponentRange;
    private int[] alphaComponentRange;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public RandomRangeColorGenerator getRangeColorGen() {
        rangeColorGen = new RandomRangeColorGenerator(redComponentRange, greenComponentRange,
                blueComponentRange, alphaComponentRange);
        return rangeColorGen;
    }

    public void setRangeColorGen(RandomRangeColorGenerator rangeColorGen) {
        this.rangeColorGen = rangeColorGen;
    }

    public int[] getRedComponentRange() {
        return redComponentRange;
    }

    public void setRedComponentRange(int[] redComponentRange) {
        this.redComponentRange = redComponentRange;
    }

    public int[] getGreenComponentRange() {
        return greenComponentRange;
    }

    public void setGreenComponentRange(int[] greenComponentRange) {
        this.greenComponentRange = greenComponentRange;
    }

    public int[] getBlueComponentRange() {
        return blueComponentRange;
    }

    public void setBlueComponentRange(int[] blueComponentRange) {
        this.blueComponentRange = blueComponentRange;
    }

    public int[] getAlphaComponentRange() {
        return alphaComponentRange;
    }

    public void setAlphaComponentRange(int[] alphaComponentRange) {
        this.alphaComponentRange = alphaComponentRange;
    }
}
