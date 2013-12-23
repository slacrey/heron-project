package com.loadburn.heron.storage.exceptions;

import com.loadburn.heron.exceptions.HeronException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-13
 */
public class StorageException extends HeronException {
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(Throwable cause) {
        super(cause);
    }
}
