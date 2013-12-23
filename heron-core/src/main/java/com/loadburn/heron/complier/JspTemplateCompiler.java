package com.loadburn.heron.complier;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.loadburn.heron.bind.Response;
import com.loadburn.heron.render.Renderable;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-28
 */
@Singleton
public class JspTemplateCompiler implements TemplateCompiler {

    public static final String PAGE_FLOW_REQUEST_ATTRIBUTE_NAME = "pageFlow";

    @Inject
    private Provider<HttpServletRequest> httpServletRequestProvider;
    @Inject
    private Provider<HttpServletResponse> httpServletResponseProvider;

    @Override
    public Renderable compile(final TemplateLoader.TemplatePage templatePage) {
        Renderable renderable = new Renderable() {

            @Override
            public void render(Object bound, Response respond) {

                HttpServletRequest servletRequest = httpServletRequestProvider.get();
                HttpServletResponse servletResponse = httpServletResponseProvider.get();

                servletRequest.setAttribute(PAGE_FLOW_REQUEST_ATTRIBUTE_NAME, bound);

                RequestDispatcher requestDispatcher = servletRequest.getRequestDispatcher(templatePage.getTemplate().getName());
                try {
                    requestDispatcher.forward(servletRequest, servletResponse);
                } catch (ServletException e) {
                    throw new RuntimeException("Could not forward the JSP response for path=" + templatePage.getTemplate().getName(), e);
                } catch (IOException e) {
                    throw new RuntimeException("Could not forward the JSP response for path=" + templatePage.getTemplate().getName(), e);
                }

            }
        };

        return renderable;
    }
}
