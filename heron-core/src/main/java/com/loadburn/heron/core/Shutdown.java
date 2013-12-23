package com.loadburn.heron.core;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.loadburn.heron.plugin.Plugin;

import java.util.List;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-15
 */
public class Shutdown {

    private final Injector injector;

    @Inject
    public Shutdown(Injector injector) {
        this.injector = injector;
    }

    public void shutdown() {
        List<Binding<Plugin>> bindings = injector.findBindingsByType(Bootstrap.PLUGIN_TYPE);
        for (Binding<Plugin> binding : bindings) {
            injector.getInstance(binding.getKey()).shutdown();
        }
    }
}
