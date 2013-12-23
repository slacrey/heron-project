package com.loadburn.heron.route;

import com.loadburn.heron.bind.Request;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-16
 */
public interface Action {

    boolean canCall(Request request);

    Object call(Request request, Object page, Map<String, String> map) throws IOException;

    Method getMethod();

}
