package com.loadburn.heron.storage.annotations;

import java.lang.annotation.*;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-31
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Transactional {

    Class<? extends Exception>[] rollbackOn() default RuntimeException.class;

    Class<? extends Exception>[] ignore() default { };

}
