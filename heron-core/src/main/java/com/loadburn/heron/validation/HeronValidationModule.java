package com.loadburn.heron.validation;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.apache.bval.guice.ValidationModule;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-25
 */
public class HeronValidationModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ValidationModule());
        bind(HeronValidator.class).to(HeronBValValidator.class).in(Scopes.SINGLETON);
    }

}
