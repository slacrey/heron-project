package com.loadburn.heron.core;

import com.google.inject.ImplementedBy;
import com.google.inject.TypeLiteral;
import com.loadburn.heron.plugin.Plugin;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-15
 */
@ImplementedBy(BootstrapImpl.class)
public interface Bootstrap {

    TypeLiteral<Plugin> PLUGIN_TYPE = new TypeLiteral<Plugin>(){};

    public void startup();

}
