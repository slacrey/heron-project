package com.loadburn.heron.captcha.config.color;

import java.awt.*;
import java.io.Serializable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-18
 */
public class GuiceColor implements Serializable {

    private static final long serialVersionUID = -2807731847556045300L;
    private Color color;
    private int red;
    private int green;
    private int blue;

    public Color getColor() {
        return new Color(red, green, blue);
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }
}
