package com.loadburn.heron.captcha.config.background;

import com.loadburn.heron.captcha.config.color.GuiceColor;
import com.octo.captcha.component.image.backgroundgenerator.MultipleShapeBackgroundGenerator;

import java.io.Serializable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-19
 */
public class GuiceBackGroundGen implements Serializable {

    private static final long serialVersionUID = -2139047534490213718L;
    private String className;
    private MultipleShapeBackgroundGenerator backGroundGen;

    private int width;
    private int height;

    private GuiceColor firstEllipseColor;
    private GuiceColor secondEllipseColor;

    private int spaceBetweenLine;
    private int spaceBetweenCircle;
    private int ellipseHeight;
    private int ellipseWidth;

    private GuiceColor firstRectangleColor;
    private GuiceColor secondRectangleColor;

    private int rectangleWidth;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public MultipleShapeBackgroundGenerator getBackGroundGen() {

        return new MultipleShapeBackgroundGenerator(width, height, firstEllipseColor.getColor(),
                secondEllipseColor.getColor(), spaceBetweenLine, spaceBetweenCircle, ellipseHeight,
                ellipseWidth, firstRectangleColor.getColor(), secondRectangleColor.getColor(), rectangleWidth);

    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public GuiceColor getFirstEllipseColor() {
        return firstEllipseColor;
    }

    public void setFirstEllipseColor(GuiceColor firstEllipseColor) {
        this.firstEllipseColor = firstEllipseColor;
    }

    public GuiceColor getSecondEllipseColor() {
        return secondEllipseColor;
    }

    public void setSecondEllipseColor(GuiceColor secondEllipseColor) {
        this.secondEllipseColor = secondEllipseColor;
    }

    public int getSpaceBetweenLine() {
        return spaceBetweenLine;
    }

    public void setSpaceBetweenLine(int spaceBetweenLine) {
        this.spaceBetweenLine = spaceBetweenLine;
    }

    public int getSpaceBetweenCircle() {
        return spaceBetweenCircle;
    }

    public void setSpaceBetweenCircle(int spaceBetweenCircle) {
        this.spaceBetweenCircle = spaceBetweenCircle;
    }

    public int getEllipseHeight() {
        return ellipseHeight;
    }

    public void setEllipseHeight(int ellipseHeight) {
        this.ellipseHeight = ellipseHeight;
    }

    public int getEllipseWidth() {
        return ellipseWidth;
    }

    public void setEllipseWidth(int ellipseWidth) {
        this.ellipseWidth = ellipseWidth;
    }

    public GuiceColor getFirstRectangleColor() {
        return firstRectangleColor;
    }

    public void setFirstRectangleColor(GuiceColor firstRectangleColor) {
        this.firstRectangleColor = firstRectangleColor;
    }

    public GuiceColor getSecondRectangleColor() {
        return secondRectangleColor;
    }

    public void setSecondRectangleColor(GuiceColor secondRectangleColor) {
        this.secondRectangleColor = secondRectangleColor;
    }

    public int getRectangleWidth() {
        return rectangleWidth;
    }

    public void setRectangleWidth(int rectangleWidth) {
        this.rectangleWidth = rectangleWidth;
    }
}
