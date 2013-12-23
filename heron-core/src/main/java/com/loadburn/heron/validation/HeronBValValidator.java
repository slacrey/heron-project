package com.loadburn.heron.validation;

import com.google.inject.Singleton;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-25
 */
@Singleton
public class HeronBValValidator implements HeronValidator {

    @Inject
    private Validator validator;

    @Override
    public Set<? extends ConstraintViolation<?>> validate(Object object) {
        return this.validator.validate(object);
    }
}
