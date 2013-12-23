package com.loadburn.heron.complier;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-27
 */
public class Template {

    private final String templateName;
    private final String text;
    private final TemplateSource source;

    public Template(String templateName, String text, TemplateSource source) {
        this.templateName = templateName;
        this.text = text;
        this.source = source;
    }

    public String getName() {
        return templateName;
    }

    public String getText() {
        return text;
    }

    public TemplateSource getTemplateSource() {
        return source;
    }
}