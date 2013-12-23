package com.loadburn.heron.email;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.apache.commons.mail.MultiPartEmail;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-24
 */
@Singleton
public class MultiPartEmailProvider implements Provider<MultiPartEmail> {

    private final EmailConfigLoader configLoader;

    @Inject
    public MultiPartEmailProvider(EmailConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    @Override
    public MultiPartEmail get() {
        return configLoader.instanceEmail(new MultiPartEmail());
    }
}
