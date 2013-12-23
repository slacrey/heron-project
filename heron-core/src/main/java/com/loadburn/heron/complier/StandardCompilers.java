package com.loadburn.heron.complier;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.loadburn.heron.annotations.Heron;
import com.loadburn.heron.render.Renderable;
import com.loadburn.heron.route.HeronPage;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-28
 */
@Singleton
public class StandardCompilers implements Compilers {

    private final HeronPage wildfire;
    private final Map<String, Class<? extends Annotation>> httpMethods;
    private final TemplateLoader loader;

    @Inject
    public StandardCompilers(HeronPage wildfire, @Heron Map<String, Class<? extends Annotation>> httpMethods, TemplateLoader loader) {
        this.wildfire = wildfire;
        this.httpMethods = httpMethods;
        this.loader = loader;
    }

    @Override
    public void compilePage(HeronPage.Page page) {

        Renderable widget = loader.compile(page);
        page.apply(widget);

    }

    @Override
    public Renderable compile(Class<?> templateClass) {
        return loader.compile(templateClass);
    }

}
