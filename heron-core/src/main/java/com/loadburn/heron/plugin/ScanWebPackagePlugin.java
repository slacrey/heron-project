package com.loadburn.heron.plugin;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.loadburn.heron.annotations.Decoration;
import com.loadburn.heron.annotations.Heron;
import com.loadburn.heron.annotations.Path;
import com.loadburn.heron.annotations.Show;
import com.loadburn.heron.complier.Compilers;
import com.loadburn.heron.complier.Templates;
import com.loadburn.heron.route.HeronPage;
import com.loadburn.heron.utils.generics.ClassesUtils;

import java.util.List;
import java.util.Set;

import static com.google.inject.matcher.Matchers.annotatedWith;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-18
 */
public class ScanWebPackagePlugin implements Plugin {

    private final HeronPage wildfire;
    private final List<Package> packages;
    private final Compilers compilers;

    @Inject
    private final Templates templates = null;

    @Inject
    private final Injector injector = null;

    @Inject
    private final Stage currentStage = null;

    @Inject
    public ScanWebPackagePlugin(HeronPage wildfire, @Heron List<Package> packages, Compilers compilers) {
        this.wildfire = wildfire;
        this.packages = packages;
        this.compilers = compilers;
    }

    @Override
    public void startup() {
        Set<Class<?>> set = Sets.newHashSet();
        for (Package pkg : packages) {

            set.addAll(ClassesUtils.matching(
                    annotatedWith(Path.class).or(
                            annotatedWith(Show.class))
            ).in(pkg));

        }

        Set<HeronPage.Page> pagesToCompile = scanPagesToCompile(set);
        extendedPages(pagesToCompile);

        if (Stage.DEVELOPMENT != currentStage) {
            compileToPages(pagesToCompile);
        }
    }

    @Override
    public void shutdown() {

    }

    private Set<HeronPage.Page> scanPagesToCompile(Set<Class<?>> set) {
        Set<HeronPage.Page> pagesToCompile = Sets.newHashSet();
        Set<Templates.TemplateDescription> templates = Sets.newHashSet();
        for (Class<?> pageClass : set) {
            Path path = pageClass.getAnnotation(Path.class);
            if (null != path) {
                pagesToCompile.addAll(wildfire.path(path.value(), pageClass));
            }
            if (pageClass.isAnnotationPresent(Show.class)) {
                templates.add(new Templates.TemplateDescription(pageClass,
                        pageClass.getAnnotation(Show.class).value()));
            }
        }

        // 预加载页面模板
        if (Stage.DEVELOPMENT != currentStage) {
            this.templates.loadAll(templates);
        }

        return pagesToCompile;
    }

    private void extendedPages(Set<HeronPage.Page> pagesToCompile) {
        Set<HeronPage.Page> pageExtensions = Sets.newHashSet();
        for (HeronPage.Page page : pagesToCompile) {
            if (page.pageClass().isAnnotationPresent(Decoration.class)) {
                analyseExtension(pageExtensions, page.pageClass());
            }
        }
        pagesToCompile.addAll(pageExtensions);
    }

    private void analyseExtension(Set<HeronPage.Page> pagesToCompile, final Class<?> extendClassArgument) {
        // store the page with a special page name used by ExtendWidget
        Class<?> extendClass = extendClassArgument;
        pagesToCompile.add(wildfire.decorate(extendClass));

        while (extendClass != Object.class) {
            extendClass = extendClass.getSuperclass();
            if (extendClass.isAnnotationPresent(Decoration.class)) {
                analyseExtension(pagesToCompile, extendClass);
            } else if (extendClass.isAnnotationPresent(Show.class)) {
                return;
            }
        }
        throw new IllegalStateException("Could not find super class annotated with @Show on parent of class: " + extendClassArgument);
    }

    private void compileToPages(Set<HeronPage.Page> pagesToCompile) {
        for (HeronPage.Page page : pagesToCompile) {
            compilers.compilePage(page);
        }
    }

}
