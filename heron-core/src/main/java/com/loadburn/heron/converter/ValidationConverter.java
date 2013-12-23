package com.loadburn.heron.converter;

import com.google.common.collect.Lists;

import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-29
 */
@Singleton
public class ValidationConverter extends ConverterAdaptor<Set<? extends ConstraintViolation<?>>, List<String>> {
    @Override
    public List<String> to(Set<? extends ConstraintViolation<?>> source) {
        List<String> errors = Lists.newArrayList();
        if (source != null) {
            for (ConstraintViolation<?> cv: source) {
                errors.add(cv.getMessage());
            }
        }
        return errors;
    }
}
