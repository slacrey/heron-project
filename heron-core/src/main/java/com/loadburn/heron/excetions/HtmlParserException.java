package com.loadburn.heron.excetions;

import com.loadburn.heron.exceptions.HeronException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-4
 */
public class HtmlParserException extends HeronException {
    public HtmlParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public HtmlParserException(String message) {
        super(message);
    }

    public HtmlParserException(Throwable cause) {
        super(cause);
    }
}
