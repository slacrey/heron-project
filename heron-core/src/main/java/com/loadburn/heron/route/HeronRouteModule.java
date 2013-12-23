package com.loadburn.heron.route;

import com.google.inject.AbstractModule;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-26
 */
public class HeronRouteModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HeronPage.class)
                .to(DefaultHeronPage.class);
        bind(RoutingDispatcher.class)
                .to(DefaultRoutingDispatcher.class);
    }
}
