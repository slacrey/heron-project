package com.loadburn.heron.exceptions;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-30
 */
public class HeronException extends RuntimeException {
    public HeronException(String message, Throwable cause) {
        super(message, cause);
    }

    public HeronException(String message) {
        super(message);
    }

    public HeronException(Throwable cause) {
        super(cause);
    }

}
