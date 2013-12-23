package com.loadburn.heron.core;

import com.google.inject.servlet.ServletModule;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-18
 */
public class HeronServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        configureBeforeFilter();
        filter("/*").through(HeronFilter.class);
        configureAfterFilter();
    }

    protected void configureBeforeFilter() {

    }

    protected void configureAfterFilter() {

    }
}
