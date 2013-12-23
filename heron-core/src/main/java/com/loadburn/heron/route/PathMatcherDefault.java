package com.loadburn.heron.route;

import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-26
 */
@Immutable
class PathMatcherDefault implements PathMatcher {
    private final List<PathMatcher> path;
    private static final String PATH_SEPARATOR = "/";

    public PathMatcherDefault(String path) {
        this.path = toMatchChain(path);
    }

    private static List<PathMatcher> toMatchChain(String path) {
        String[] pieces = path.split(PATH_SEPARATOR);

        List<PathMatcher> matchers = new ArrayList<PathMatcher>();
        for (String piece : pieces) {
            matchers.add((piece.startsWith(":")) ? new ComplexPathMatcher(piece) : new SimplePathMatcher(piece));
        }

        return Collections.unmodifiableList(matchers);
    }

    public String name() {
        return null;
    }

    public boolean matches(String incoming) {
        final Map<String, String> map = findMatches(incoming);
        return null != map;
    }

    public Map<String, String> findMatches(String incoming) {
        String[] pieces = incoming.split(PATH_SEPARATOR);
        int i = 0;

        if (path.size() > pieces.length)
            return null;

        Map<String, String> values = new HashMap<String, String>();
        for (PathMatcher pathMatcher : path) {

            if (i == pieces.length)
                return pathMatcher.matches("") ? values : null;

            String piece = pieces[i];

            if (!pathMatcher.matches(piece))
                return null;

            //store variable as needed
            final String name = pathMatcher.name();
            if (null != name)
                values.put(name, piece);

            //next piece
            i++;
        }

        return (i == pieces.length) ? values : null;
    }

    @Immutable
    static class SimplePathMatcher implements PathMatcher {
        private final String path;

        SimplePathMatcher(String path) {
            this.path = path;
        }

        public boolean matches(String incoming) {
            return path.equals(incoming);
        }

        @NotNull
        public Map<String, String> findMatches(String incoming) {
            return Collections.emptyMap();
        }

        public String name() {
            return null;
        }
    }

    @Immutable
    static class ComplexPathMatcher implements PathMatcher {
        private final String variable;

        public ComplexPathMatcher(String piece) {
            this.variable = piece.substring(1);
        }

        public boolean matches(String incoming) {
            return true;
        }

        @NotNull
        public Map<String, String> findMatches(String incoming) {
            return Collections.emptyMap();
        }

        public String name() {
            return variable;
        }
    }

    @Immutable
    static class IgnoringPathMatcher implements PathMatcher {
        public boolean matches(String incoming) {
            return false;
        }

        @NotNull
        public Map<String, String> findMatches(String incoming) {
            return Collections.emptyMap();
        }

        public String name() {
            return "";
        }
    }

    static PathMatcher ignoring() {
        return new IgnoringPathMatcher();
    }

}
