package com.loadburn.heron.annotations;

import com.loadburn.heron.transport.Transport;

import java.lang.annotation.*;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Documented
public @interface As {

    public abstract Class<? extends Transport> value();

}
