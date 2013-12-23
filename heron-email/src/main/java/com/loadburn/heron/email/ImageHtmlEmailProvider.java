package com.loadburn.heron.email;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.commons.mail.ImageHtmlEmail;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-24
 */
public class ImageHtmlEmailProvider implements Provider<ImageHtmlEmail> {

    private final EmailConfigLoader configLoader;

    @Inject
    public ImageHtmlEmailProvider(EmailConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    @Override
    public ImageHtmlEmail get() {
        return configLoader.instanceEmail(new ImageHtmlEmail());
    }
}
