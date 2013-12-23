package com.loadburn.heron.complier;

import com.loadburn.heron.excetions.HtmlParserException;
import com.loadburn.heron.enums.CharsetEnum;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-4
 */
public class HtmlParser {

    private static final String ATTR_TARGET = "target";
    private static final String ATTR_BODY = "body";
    private Document document;
    private Elements decoratorElements;

    public HtmlParser(String html) {
        document = Jsoup.parse(html);
    }

    public HtmlParser(File file) {
        try {
            document = Jsoup.parse(file, CharsetEnum.UTF8.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HtmlParser decorator() {
        if (document == null) {
            throwException("Document对象为空");
        }
        decoratorElements = document.getElementsByTag("decorator");
        return this;
    }

    public String title() {
        if (document == null) {
            throwException("Document对象为空");
        }
        return document.title();
    }

    public void title(String text) {
        if (document == null) {
            throwException("Document对象为空");
        }
        document.title(text);
    }

    private void throwException(String message) {
        HtmlParserException exception = new HtmlParserException(message);
        throw exception;
    }

    public String head() {
        return document.head().html();
    }

    public String body() {
        return document.body().html();
    }

    public HtmlParser decoratorBody() {
        if (decoratorElements == null) {
            throwException("装饰Element不存在");
        }
        String lineSeparator = System.getProperty("line.separator", "\\n");
        Element element = null;
        Iterator<Element> elementIterator = decoratorElements.iterator();
        while (elementIterator.hasNext()) {
            Element iteratorElement = elementIterator.next();
            if (iteratorElement.hasAttr(ATTR_TARGET) && ATTR_BODY.equals(iteratorElement.attr(ATTR_TARGET))) {
                element = iteratorElement;
                element.after(lineSeparator + "===========================================");
            }
        }
        element.remove();
        return this;
    }

    public String html() {
        return document.html();
    }

    public Document getDocument() {
        return document;
    }

}
