package com.loadburn.heron.excetions;

import com.loadburn.heron.exceptions.HeronException;

public class TemplateLoaderException extends HeronException {
    public TemplateLoaderException(String msg, Exception e) {
        super(msg, e);
    }

    public TemplateLoaderException(String msg) {
        super(msg);
    }
}
