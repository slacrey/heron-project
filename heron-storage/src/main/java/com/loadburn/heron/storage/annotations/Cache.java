package com.loadburn.heron.storage.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-3
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface Cache {
    String region() default "";
}
