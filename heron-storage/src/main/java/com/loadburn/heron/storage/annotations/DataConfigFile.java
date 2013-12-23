package com.loadburn.heron.storage.annotations;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.*;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface DataConfigFile {
}
