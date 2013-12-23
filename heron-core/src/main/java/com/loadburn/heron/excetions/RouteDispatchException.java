package com.loadburn.heron.excetions;

import com.loadburn.heron.exceptions.HeronException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-16
 */
public class RouteDispatchException extends HeronException {
    public RouteDispatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
