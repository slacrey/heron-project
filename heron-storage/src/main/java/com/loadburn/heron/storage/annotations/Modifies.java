package com.loadburn.heron.storage.annotations;

import com.loadburn.heron.storage.config.DataBaseType;

import java.lang.annotation.*;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Modifies {
    DataBaseType scheme() default DataBaseType.MySQL;

    Modify[] value();
}
