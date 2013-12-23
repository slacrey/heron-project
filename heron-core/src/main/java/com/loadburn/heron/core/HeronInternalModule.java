package com.loadburn.heron.core;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.RequestScoped;
import com.loadburn.heron.bind.*;
import com.loadburn.heron.render.Model;
import com.loadburn.heron.render.ModelProvider;
import org.apache.commons.fileupload.FileItem;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-25
 */
public class HeronInternalModule extends AbstractModule {
    @Override
    protected void configure() {
//        bindScope(RequestScoped.class, REQUEST);
//        bindScope(SessionScoped.class, SESSION);
//        bind(ServletRequest.class).to(HttpServletRequest.class);
//        bind(ServletResponse.class).to(HttpServletResponse.class);

        bind(new TypeLiteral<Request<String>>(){}).toProvider(RequestProvider.class).in(RequestScoped.class);
        bind(new TypeLiteral<RequestBinder<String>>() {}).to(MvelRequestBinder.class).asEagerSingleton();
        bind(new TypeLiteral<RequestBinder<FileItem>>() {}).to(MultiPartRequestBinder.class).asEagerSingleton();
        bind(FlashCache.class).to(HttpSessionFlashCache.class);
        bind(new TypeLiteral<Model<String, Object>>(){}).toProvider(ModelProvider.class);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof HeronInternalModule;
    }

    @Override
    public int hashCode() {
        return HeronInternalModule.class.hashCode();
    }

}
