package com.loadburn.heron.validation;

import com.google.inject.ImplementedBy;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-25
 */
@ImplementedBy(HeronBValValidator.class)
public interface HeronValidator {
    Set<? extends ConstraintViolation<?>> validate(Object object);
}
