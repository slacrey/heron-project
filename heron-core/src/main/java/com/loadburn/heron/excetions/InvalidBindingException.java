package com.loadburn.heron.excetions;

import com.loadburn.heron.exceptions.HeronException;

public class InvalidBindingException extends HeronException {
    public InvalidBindingException(String msg) {
        super(msg);
    }
}