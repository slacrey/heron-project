package com.loadburn.heron.captcha.bind;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-19
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface CaptchaConfig {
}