package com.loadburn.heron.email;

import com.google.inject.Inject;
import com.loadburn.heron.plugin.Plugin;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-25
 */
public class EmailConfigLoaderPlugin implements Plugin {

    private final EmailConfigLoader loader;

    @Inject
    public EmailConfigLoaderPlugin(EmailConfigLoader loader) {
        this.loader = loader;
    }

    @Override
    public void startup() {
        loader.loadEmailConfig();
    }

    @Override
    public void shutdown() {

    }
}
