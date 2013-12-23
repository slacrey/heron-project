package com.loadburn.heron.exceptions;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
public class ConfigLoaderException extends HeronException {

    public ConfigLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigLoaderException(String message) {
        super(message);
    }

    public ConfigLoaderException(Throwable cause) {
        super(cause);
    }
}
