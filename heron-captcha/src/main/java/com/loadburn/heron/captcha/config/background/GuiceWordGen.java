package com.loadburn.heron.captcha.config.background;

import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;

import java.io.Serializable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-19
 */
public class GuiceWordGen implements Serializable {

    private static final long serialVersionUID = 8846127718542580313L;
    private RandomWordGenerator wordGen;
    private String text;

    public RandomWordGenerator getWordGen() {
        wordGen = new RandomWordGenerator(text);
        return wordGen;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
