package com.loadburn.heron.storage.annotations;

import com.loadburn.heron.storage.config.DataBaseType;

import java.lang.annotation.*;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-31
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Queries {
    DataBaseType scheme() default DataBaseType.MySQL;

    Query[] value();

    boolean cache() default false;
}
