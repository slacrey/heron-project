package com.loadburn.heron.plugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.name.Names;

import java.util.Set;
import java.util.UUID;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-17
 */
public abstract class PluginModule extends AbstractModule {

    private Set<Class<? extends Plugin>> classAware = Sets.newHashSet();

    @Override
    protected void configure() {
        configureAware(classAware);
        for (Class<? extends Plugin> aware : classAware) {
            addAware(aware);
        }
    }

    protected abstract void configureAware(Set<Class<? extends Plugin>> classAware);

    protected ScopedBindingBuilder addAware(Class<? extends Plugin> aware) {
        Preconditions.checkArgument(!Plugin.class.equals(aware), "Can't bind to interface Aware");
        return bind(Key.get(Plugin.class, Names.named(UUID.randomUUID().toString()))).to(aware);
    }

}
