package com.loadburn.heron.bind;

import java.util.List;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-29
 */
public interface Response {

    void write(String text);

    void write(char c);

    String toString();

    void writeToHead(String text);

    void redirect(String to);

    String getContentType();

    String getRedirect();

    String getHead();

    String getBody();

    void writeToBody(String text);

    void clear();

    Object pageObject();

    List<String> getErrors();

    void setErrors(List<String> errors);

}
