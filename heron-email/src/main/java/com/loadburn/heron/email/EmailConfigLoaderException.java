package com.loadburn.heron.email;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-24
 */
public class EmailConfigLoaderException extends RuntimeException {

    public EmailConfigLoaderException(String message) {
        super(message);
    }

    public EmailConfigLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

}
