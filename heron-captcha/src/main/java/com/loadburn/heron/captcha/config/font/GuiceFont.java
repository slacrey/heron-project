package com.loadburn.heron.captcha.config.font;

import java.awt.*;
import java.io.Serializable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-19
 */
public class GuiceFont implements Serializable {

    private static final long serialVersionUID = -4371593240122376083L;
    private Font font;
    private String name;
    private int style;
    private int size;

    public Font getFont() {
        return new Font(name, style, size);
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
