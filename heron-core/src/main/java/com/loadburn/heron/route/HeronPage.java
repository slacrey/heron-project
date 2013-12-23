package com.loadburn.heron.route;

import com.google.inject.ImplementedBy;
import com.loadburn.heron.annotations.Show;
import com.loadburn.heron.bind.Request;
import com.loadburn.heron.render.Renderable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-26
 */
@ImplementedBy(DefaultHeronPage.class)
public interface HeronPage {

    Set<Page> path(String uri, Class<?> pageClass);

    Page get(String uri);

    Page forName(String name);

    Page forInstance(Object instance);

    Page forClass(Class<?> pageClass);

    Collection<List<Page>> getPageMap();

    Page decorate(Class<?> pageClass);

    public static interface Page extends Comparable<Page> {

        Object instantiate();

        Object doMethod(String httpMethod, Object page, String pathInfo, Request request)
                throws IOException;

        Class<?> pageClass();

        String getUri();

        Set<String> getMethod();

        Show getShow();

        Renderable widget();

        void apply(Renderable widget);

        boolean isDecorated();

    }

}
