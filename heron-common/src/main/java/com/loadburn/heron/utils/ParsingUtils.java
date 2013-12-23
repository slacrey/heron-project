package com.loadburn.heron.utils;

import com.google.inject.Inject;
import com.loadburn.heron.utils.converter.TypeConverter;

import java.util.*;
import java.util.regex.Pattern;

public class ParsingUtils {

    private ParsingUtils() {
    }

    private static TypeConverter converter;

    public static TypeConverter getTypeConverter() {
        return ParsingUtils.converter;
    }

    @Inject
    public static void setTypeConverter(TypeConverter converter) {
        ParsingUtils.converter = converter;
    }

    @SuppressWarnings("UnnecessaryUnboxing")
    public static Map<String, String> toBindMap(String expression) {
        if (StringUtils.empty(expression))
            return Collections.emptyMap();

        Deque<Character> escapes = new ArrayDeque<Character>();
        List<String> pairs = new ArrayList<String>();
        int index = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (('"' == c && (escapes.isEmpty() || escapes.peek().charValue() != c))
                    || '[' == c || '{' == c || '(' == c) {
                escapes.push(c);
            } else if (('"' == c && escapes.peek().charValue() == c) || ']' == c || '}' == c || ')' == c) {
                escapes.pop();
            }

            if (escapes.isEmpty() && ',' == c) {
                if (index < i)
                    pairs.add(expression.substring(index, i));

                for (; i < expression.length() && (',' == expression.charAt(i) || ' ' == expression.charAt(i)); )
                    i++;

                index = i;
            }

        }

        if (index < expression.length()) {

            for (; ',' == expression.charAt(index) || ' ' == expression.charAt(index); index++) ;

            final String pair = expression.substring(index, expression.length()).trim();

            if (pair.length() > 1) {
                pairs.add(pair);
            }
        }

        final Map<String, String> map = new LinkedHashMap<String, String>();
        for (String pair : pairs) {
            final String[] nameAndValue = pair.split("=", 2);

            if (nameAndValue.length != 2)
                throw new IllegalArgumentException("Invalid parameter binding format: " + pair);

            StringUtils.nonEmpty(nameAndValue[0], "Cannot have an empty left hand side target parameter: " + pair);
            StringUtils.nonEmpty(nameAndValue[1], "Must provide a non-empty right hand side expression: " + pair);

            map.put(nameAndValue[0].trim(), nameAndValue[1].trim());
        }

        return Collections.unmodifiableMap(map);
    }

    public static String stripExpression(String expr) {
        return expr.substring(2, expr.length() - 1);
    }

    public static boolean isExpression(String attribute) {
        return attribute.startsWith("${");
    }


    public static String stripQuotes(String var) {
        return var.substring(1, var.length() - 1);
    }

    public static boolean treatAsXml(String template) {
        return 0 > indexOfMeta(template);
    }

    public static int indexOfMeta(String template) {
        for (int i = 0; i < template.length(); i++) {
            char c = template.charAt(i);

            if (isWhitespace(c))
                continue;

            if ('@' == c) {
                final char trailing = template.charAt(i + 5);

                if ("Meta".equals(template.substring(i + 1, i + 5))

                        && ('(' == trailing || isWhitespace(trailing)))

                    return i;
            }

            return -1;
        }

        return -1;
    }

    private static boolean isWhitespace(char c) {
        return ' ' == c || '\n' == c || '\r' == c || '\t' == c;
    }

    private static enum TokenizerState {
        READING_TEXT, READING_EXPRESSION
    }

    private final static Pattern URI_REGEX
            = Pattern.compile("(([a-zA-Z][0-9a-zA-Z+\\-\\.]*:)?/{0,2}[0-9a-zA-Z;/?:@&=+$\\.\\-_!~*'()%]+)?(#[0-9a-zA-Z;/?:@&=+$\\.\\-_!~*'()%]+)?");

    private final static Pattern TEMPLATE_URI_PATTERN = Pattern.compile("(([a-zA-Z][0-9a-zA-Z+\\\\-\\\\.]*:)?/{0,2}[0-9a-zA-Z;" +
            "/?:@&=+$\\\\.\\\\-_!~*'()%]+)?(#[0-9a-zA-Z;/?:@&=+$\\\\.\\\\-_!~*'()%]+)?");

    public static boolean isValidURI(String uri) {
        return (null != uri)
                && URI_REGEX
                .matcher(uri)
                .matches();
    }

}