package com.loadburn.heron.storage.sql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-16
 */
public class QueryUtils {

    /**
     *
     * @param source
     * @param regex
     * @param statement
     * @return
     */
    public static String replaceFirstStatement(String source, String regex, String statement) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern == null ? null : pattern.matcher(source);
        return matcher == null ? null : matcher.replaceFirst(statement);
    }

}
