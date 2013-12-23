package com.loadburn.heron.storage.exceptions;

import com.loadburn.heron.exceptions.HeronException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-13
 */
public class LoadDriverException extends HeronException {
    public LoadDriverException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadDriverException(String message) {
        super(message);
    }

    public LoadDriverException(Throwable cause) {
        super(cause);
    }
}
