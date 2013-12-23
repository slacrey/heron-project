package com.loadburn.heron.storage.annotations;

import java.lang.annotation.*;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-31
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Query {
    String name();

    String query();
}
