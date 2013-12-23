package com.loadburn.heron.bind;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.loadburn.heron.excetions.NoSuchResourceException;
import net.jcip.annotations.NotThreadSafe;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-16
 */
@NotThreadSafe
public class DefaultResponse implements Response {

    private static final String TEXT_TAG_TEMPLATE = "wildfire.template.textfield";
    private static final String TEXTAREA_TAG_TEMPLATE = "wildfire.template.textarea";

    public static final String HERON_HEADER_PLACEHOLDER = "_@_HeRoN:HeAdEr:PlAcE:HoLdEr_@_";
    public static final String HERON_BODY_PLACEHOLDER = "_@_HeRoN:BoDy:PlAcE:HoLdEr_@_";

    private static final String HERON_HEADER_PATTERN_PLACEHOLDER = "<!--[\\w\\W\\r\\n]*?decorator[\\w\\W\\r\\n]*?:[\\w\\W\\r\\n]*?head[\\w\\W\\r\\n]*?-->";
    private static final String HERON_BODY_PATTERN_PLACEHOLDER = "<!--[\\w\\W\\r\\n]*?decorator[\\w\\W\\r\\n]*?:[\\w\\W\\r\\n]*?body[\\w\\W\\r\\n]*?-->";

    private static final HashMap<String, Pattern> patternHashMap = Maps.newHashMap();

    private static final AtomicReference<Map<String, String>> templates =
            new AtomicReference<Map<String, String>>();

    private static final String TEXT_HTML = "text/html;charset=utf-8";
    private Object page;

    private List<String> errors;

    @SuppressWarnings("unchecked")
    public DefaultResponse(Object context) {
        this.page = context;
        if (null == templates.get()) {
            final Properties properties = new Properties();
            try {
                InputStream inputStream = DefaultResponse.class.getResourceAsStream("templates.properties");
                if (inputStream != null) {
                    properties.load(inputStream);
                }
            } catch (IOException e) {
                throw new NoSuchResourceException("templates.properties找不到", e);
            }
            templates.compareAndSet(null, (Map) properties);
        }
        initPattern();
    }
    private void initPattern() {
        patternHashMap.put("head", Pattern.compile(HERON_HEADER_PATTERN_PLACEHOLDER, Pattern.CASE_INSENSITIVE));
        patternHashMap.put("body", Pattern.compile(HERON_BODY_PATTERN_PLACEHOLDER, Pattern.CASE_INSENSITIVE));
    }

    private final StringBuilder out = new StringBuilder();
    private final StringBuilder head = new StringBuilder();
    private final StringBuilder body = new StringBuilder();

    private final Set<String> requires = new LinkedHashSet<String>();
    private String redirect;

    public String getHead() {
        return head.toString();
    }

    @Override
    public String getBody() {
        return body.toString();
    }

    @Override
    public void writeToBody(String text) {
        body.append(text);
    }

    public void write(String text) {
        out.append(text);
    }

    public void write(char c) {
        out.append(c);
    }

    public void redirect(String to) {
        this.redirect = to;
    }

    public void writeToHead(String text) {
        head.append(text);
    }

    public String getRedirect() {
        return redirect;
    }

    public String getContentType() {
        return TEXT_HTML;
    }

    public void clear() {
        if (null != out) {
            out.delete(0, out.length());
        }
        if (null != head) {
            head.delete(0, head.length());
        }
    }

    @Override
    public Object pageObject() {
        return page;
    }

    @Override
    public List<String> getErrors() {
        if (this.errors == null) {
            this.errors = Lists.newArrayList();
        }
        return this.errors;
    }

    @Override
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        for (String require : requires) {
            writeToHead(require);
        }
        String output = out.toString();
        Pattern headPattern = patternHashMap.get("head");
        Matcher headMatcher = headPattern.matcher(output);
        output = headMatcher.replaceFirst(head.toString());

        Pattern bodyPattern = patternHashMap.get("body");
        Matcher bodyMatcher = bodyPattern.matcher(output);
        output = bodyMatcher.replaceFirst(body.toString());

        return output;
    }

}
