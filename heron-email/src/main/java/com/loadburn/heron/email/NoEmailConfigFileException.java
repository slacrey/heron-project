package com.loadburn.heron.email;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-24
 */
public class NoEmailConfigFileException extends RuntimeException {

    public NoEmailConfigFileException(String message) {
        super(message);
    }

    public NoEmailConfigFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
