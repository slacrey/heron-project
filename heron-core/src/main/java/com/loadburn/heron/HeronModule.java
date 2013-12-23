package com.loadburn.heron;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.loadburn.heron.annotations.Get;
import com.loadburn.heron.annotations.Heron;
import com.loadburn.heron.annotations.Post;
import com.loadburn.heron.complier.HtmlTemplateCompiler;
import com.loadburn.heron.complier.JspTemplateCompiler;
import com.loadburn.heron.complier.MvelTemplateCompiler;
import com.loadburn.heron.complier.TemplateCompiler;
import com.loadburn.heron.converter.HeronConversionModule;
import com.loadburn.heron.core.HeronInternalModule;
import com.loadburn.heron.core.HeronServletModule;
import com.loadburn.heron.route.HeronRouteModule;
import com.loadburn.heron.validation.HeronValidationModule;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-25
 */
public abstract class HeronModule extends AbstractModule {

    private final List<Package> webPackages = Lists.newArrayList();
    private final Map<String, Class<? extends Annotation>> methods = Maps.newHashMap();

    public HeronModule() {
        methods.put("get", Get.class);
        methods.put("post", Post.class);
    }

    @Override
    protected void configure() {
        install(configureHeronServletModule());
        install(new HeronInternalModule());
        install(new HeronRouteModule());
        install(new HeronValidationModule());
        install(new HeronConversionModule());

        configureHeron();

        bind(new TypeLiteral<List<Package>>() {
        })
                .annotatedWith(Heron.class)
                .toInstance(webPackages);

        bind(new TypeLiteral<Map<String, Class<? extends Annotation>>>() {
        })
                .annotatedWith(Heron.class)
                .toInstance(methods);


        configureTemplateSystem();

    }

    protected final void scanWeb(Package pack) {
        Preconditions.checkArgument(null != pack, "Package 不能为空");
        webPackages.add(pack);
    }

    protected void configureTemplateSystem() {

        ImmutableMap.Builder<String, Class<? extends TemplateCompiler>> builder = ImmutableMap.builder();

        builder.put("jsp", JspTemplateCompiler.class);
        builder.put("mvel", MvelTemplateCompiler.class);
        builder.put("html", HtmlTemplateCompiler.class);

        configureTemplateCompilers(builder);

        Map<String, Class<? extends TemplateCompiler>> map = builder.build();
        bind(new TypeLiteral<Map<String, Class<? extends TemplateCompiler>>>() {
        }).toInstance(map);
    }


    protected abstract void configureHeron();

    protected abstract HeronServletModule configureHeronServletModule();

    protected void configureTemplateCompilers(ImmutableMap.Builder<String, Class<? extends TemplateCompiler>> compilers) {
    }

}
