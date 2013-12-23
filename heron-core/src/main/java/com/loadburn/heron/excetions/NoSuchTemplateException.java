package com.loadburn.heron.excetions;

import com.loadburn.heron.exceptions.HeronException;

public class NoSuchTemplateException extends HeronException {
    public NoSuchTemplateException(String msg) {
        super(msg);
    }
}
