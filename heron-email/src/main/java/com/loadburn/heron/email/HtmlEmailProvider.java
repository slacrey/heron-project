package com.loadburn.heron.email;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.loadburn.heron.utils.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.util.Properties;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-24
 */
public class HtmlEmailProvider implements Provider<HtmlEmail> {

    private final EmailConfigLoader configLoader;

    @Inject
    public HtmlEmailProvider(EmailConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    @Override
    public HtmlEmail get() {
        return configLoader.instanceEmail(new HtmlEmail());
    }
}
