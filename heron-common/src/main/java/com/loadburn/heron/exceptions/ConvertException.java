package com.loadburn.heron.exceptions;

import com.loadburn.heron.exceptions.HeronException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-13
 */
public class ConvertException extends HeronException {

    public ConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(Throwable cause) {
        super(cause);
    }
}
