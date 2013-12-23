package com.loadburn.heron.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-16
 */
public class StringUtils {

    public static void nonEmpty(String aString, String message) {
        if (empty(aString))
            throw new IllegalArgumentException(message);
    }

    public static boolean empty(String string) {
        return null == string || "".equals(string.trim());
    }

    public static String join(String[] strings, char sep) {
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string);
            builder.append(sep);
        }
        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    /**
     * 将驼峰式命名的字符串转换为下划线大写方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。</br>
     * 例如：HelloWorld->HELLO_WORLD
     *
     * @param name 转换前的驼峰式命名的字符串
     * @return 转换后下划线大写方式命名的字符串
     */
    public static String underscoreName(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            // 将第一个字符处理成大写
            result.append(name.substring(0, 1).toUpperCase());
            // 循环处理其余字符
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                // 在大写字母前添加下划线
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                // 其他字符直接转成大写
                result.append(s.toUpperCase());
            }
        }
        return result.toString();
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。</br>
     * 例如：HELLO_WORLD->HelloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String camelName(String name) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!name.contains("_")) {
            // 不含下划线，仅将首字母小写
            return name.substring(0, 1).toLowerCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String camels[] = name.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 处理真正的驼峰片段
            if (result.length() == 0) {
                // 第一个驼峰片段，全部字母都小写
                result.append(camel.toLowerCase());
            } else {
                // 其他的驼峰片段，首字母大写
                result.append(camel.substring(0, 1).toUpperCase());
                result.append(camel.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    /**
     * 替换空格
     * @param resource 原始字符串
     * @param ch 待替换的字符或字符串
     * @return 替换后的字符串
     */
    public static String replaceAs(String resource, String ch) {

        StringBuilder builder = new StringBuilder();
        int position = 0;
        char currentChar;
        while (position < resource.length()) {
            currentChar = resource.charAt(position++);
            if (!Character.isWhitespace(currentChar)) builder.append(currentChar);
            else builder.append(ch);
        }
        return builder.toString();

    }

    /**
     * 删除空格
     * @param resource 原始字符串
     * @return 整理后的字符串
     */
    public static String replaceBlank(String resource) {

        StringBuilder builder = new StringBuilder();
        int position = 0;
        char currentChar;
        while (position < resource.length()) {
            currentChar = resource.charAt(position++);
            if (!Character.isWhitespace(currentChar)) builder.append(currentChar);
        }
        return builder.toString();

    }

}
