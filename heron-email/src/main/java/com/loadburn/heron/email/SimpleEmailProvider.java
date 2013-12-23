package com.loadburn.heron.email;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.apache.commons.mail.SimpleEmail;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-24
 */
@Singleton
public class SimpleEmailProvider implements Provider<SimpleEmail> {

    private final EmailConfigLoader configLoader;

    @Inject
    public SimpleEmailProvider(EmailConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    @Override
    public SimpleEmail get() {
        return configLoader.instanceEmail(new SimpleEmail());
    }
}
