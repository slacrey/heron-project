package com.loadburn.heron.email;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.RequestScoped;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-23
 */
public class HeronEmailModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EmailConfigLoader.class);
        bind(new TypeLiteral<HtmlEmail>(){}).toProvider(HtmlEmailProvider.class).in(RequestScoped.class);
        bind(new TypeLiteral<SimpleEmail>(){}).toProvider(SimpleEmailProvider.class).in(RequestScoped.class);
        bind(new TypeLiteral<MultiPartEmail>(){}).toProvider(MultiPartEmailProvider.class).in(RequestScoped.class);
        bind(new TypeLiteral<ImageHtmlEmail>(){}).toProvider(ImageHtmlEmailProvider.class).in(RequestScoped.class);
    }

}
