package com.loadburn.heron.route;

import com.google.inject.ImplementedBy;
import com.loadburn.heron.bind.Request;

import java.io.IOException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-15
 */
@ImplementedBy(DefaultRoutingDispatcher.class)
public interface RoutingDispatcher {

    Object dispatch(Request request) throws IOException;

}
