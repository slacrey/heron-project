package com.loadburn.heron.core;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.loadburn.heron.plugin.Plugin;

import java.util.List;

import static com.google.inject.matcher.Matchers.annotatedWith;

/**
 * @author linfeng (scstlinfeng@yahoo.com)
 *         Date: 13-10-15
 */
public class BootstrapImpl implements Bootstrap {

    @Inject
    private final Injector injector = null;

    @Override
    public void startup() {
        List<Binding<Plugin>> bindings = injector.findBindingsByType(Bootstrap.PLUGIN_TYPE);
        for (Binding<Plugin> binding : bindings) {
            binding.getProvider().get().startup();
        }
    }

}
