package com.loadburn.heron.core;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.loadburn.heron.bind.Request;
import com.loadburn.heron.bind.Response;
import com.loadburn.heron.render.Result;
import com.loadburn.heron.route.RoutingDispatcher;
import net.jcip.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-18
 */
@Immutable
@Singleton
public class HeronFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(HeronFilter.class.getName());
    private final RoutingDispatcher dispatcher;
    private final Provider<Bootstrap> startup;
    private final Provider<Shutdown> shutdown;
    private final Provider<Request<String>> requestProvider;
    private final Injector injector;

    @Inject
    public HeronFilter(RoutingDispatcher dispatcher, Provider<Bootstrap> startup,
                                     Provider<Shutdown> shutdown, Provider<Request<String>> requestProvider, Injector injector) {
        this.dispatcher = dispatcher;
        this.startup = startup;
        this.shutdown = shutdown;
        this.requestProvider = requestProvider;
        this.injector = injector;
        logger.info("heron initialization...");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("heron startup...");
        startup.get().startup();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Object dispatchResult = dispatcher.dispatch(this.requestProvider.get());
        if (null != dispatchResult) {
            if (dispatchResult instanceof Response) {
                Response respond = (Response) dispatchResult;

                final String redirect = respond.getRedirect();
                if (null != redirect) {
                    response.sendRedirect(request.getContextPath() + redirect);
                } else {
                    if (null == response.getContentType()) {
                        response.setContentType(respond.getContentType());
                    }
                    response.getWriter().write(respond.toString());
                }
            } else if (dispatchResult instanceof Result) {
                Result<?> result = (Result<?>)dispatchResult;
                result.render(injector, response);
            } else {
                RuntimeException exception = new RuntimeException("方法返回类型错误,返回类型可以回String或Result.");
                logger.error("方法返回类型错误,返回类型可以回String或Result.", exception);
                throw exception;
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        logger.info("heron shutdown...");
        shutdown.get().shutdown();
    }
}
