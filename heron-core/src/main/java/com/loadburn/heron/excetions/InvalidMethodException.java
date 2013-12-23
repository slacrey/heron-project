package com.loadburn.heron.excetions;

import com.loadburn.heron.exceptions.HeronException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-16
 */
public class InvalidMethodException extends HeronException {
    public InvalidMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMethodException(String message) {
        super(message);
    }
}
