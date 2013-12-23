package com.loadburn.heron.storage;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.loadburn.heron.storage.transaction.InWork;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-11
 */
@Singleton
public final class StorageFilter implements Filter {

    private final InWork inWork;
    private final StorageService storageService;

    @Inject
    public StorageFilter(InWork inWork, StorageService storageService) {
        this.inWork = inWork;
        this.storageService = storageService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        storageService.start();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        inWork.begin();
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            inWork.end();
        }
    }

    @Override
    public void destroy() {
        storageService.stop();
    }
}
