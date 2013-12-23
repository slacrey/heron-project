package com.loadburn.heron.captcha.exceptions;

import org.apache.shiro.authc.AuthenticationException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-19
 */
public class CaptchaException extends AuthenticationException {

    private static final long serialVersionUID = -6643313830764032025L;

    public CaptchaException() {
        super();
    }

    public CaptchaException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptchaException(String message) {
        super(message);
    }

    public CaptchaException(Throwable cause) {
        super(cause);
    }

}
