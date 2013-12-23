package com.loadburn.heron.route;

import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-26
 */
interface PathMatcher {
    boolean matches(String incoming);

    String name();

    Map<String, String> findMatches(String incoming);
}
