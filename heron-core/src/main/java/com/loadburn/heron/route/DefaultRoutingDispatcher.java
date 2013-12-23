package com.loadburn.heron.route;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Stage;
import com.loadburn.heron.bind.DefaultResponse;
import com.loadburn.heron.bind.FlashCache;
import com.loadburn.heron.bind.Request;
import com.loadburn.heron.bind.RequestBinder;
import com.loadburn.heron.complier.Compilers;
import com.loadburn.heron.converter.ValidationConverter;
import com.loadburn.heron.render.Result;
import com.loadburn.heron.complier.Templates;
import net.jcip.annotations.Immutable;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Immutable
@Singleton
class DefaultRoutingDispatcher implements RoutingDispatcher {
    private final HeronPage wildfire;
    private final RequestBinder binder;
    private final Provider<FlashCache> flashCacheProvider;
    private final Compilers compilers;
    private final Templates templates;

    @Inject
    Provider<HttpServletRequest> httpServletRequestProvider;

    @Inject
    private ValidationConverter validationConvertor;

    @Inject
    private Stage currentStage;

    @Inject
    public DefaultRoutingDispatcher(HeronPage wildfire, RequestBinder<String> binder, Provider<FlashCache> flashCacheProvider,
                             Compilers compilers, Templates templates) {
        this.wildfire = wildfire;
        this.binder = binder;
        this.flashCacheProvider = flashCacheProvider;
        this.compilers = compilers;
        this.templates = templates;
    }

    public Object dispatch(Request request) throws IOException {
        String uri = request.path();

        HeronPage.Page page = flashCacheProvider.get().remove(uri);

        if (null == page) {
            page = wildfire.get(uri);
        }
        if (null == page) {
            return null;
        }

        final Object instance = page.instantiate();
        if (Stage.DEVELOPMENT == currentStage) {
            compilers.compilePage(page);
        }
        return bindAndRespond(request, page, instance);

    }

    private Object bindAndRespond(Request request, HeronPage.Page page, Object instance)
            throws IOException {
        //bind request
        binder.bind(request, instance);

        List<String> errors = null;
        Object redirect = null;
        try {
            redirect = fireEvent(request, page, instance);
        } catch (ValidationException ve) {
            ve.getCause().printStackTrace();
            ConstraintViolationException cve = (ConstraintViolationException) ve.getCause();
            Set<? extends ConstraintViolation<?>> scv = (Set<? extends ConstraintViolation<?>>) cve.getConstraintViolations();
            errors = validationConvertor.to(scv);
        }

        DefaultResponse respond = new DefaultResponse(instance);
        respond.setErrors(errors);
        if (redirect != null) {
            if (redirect instanceof String) {
                respond.redirect((String) redirect);
            } else if (redirect instanceof Class) {
                HeronPage.Page targetPage = wildfire.forClass((Class<?>) redirect);
                respond.redirect(contextualize(request, targetPage.getUri()));
            } else if (redirect instanceof Result<?>) {
                return redirect;
            } else {
                HeronPage.Page targetPage = wildfire.forInstance(redirect);
                flashCacheProvider.get().put(targetPage.getUri(), targetPage);
                respond.redirect(contextualize(request, targetPage.getUri()));
            }
        } else {
            page.widget().render(instance, respond);
        }

        return respond;
    }

    // We're sure the request parameter map is a Map<String, String[]>
    @SuppressWarnings("unchecked")
    private Object fireEvent(Request request, HeronPage.Page page, Object instance)
            throws IOException {
        final String method = request.method();
        final String pathInfo = request.path();

        return page.doMethod(method.toLowerCase(), instance, pathInfo, request);
    }

    private static String contextualize(Request request, String targetUri) {
        return request.context() + targetUri;
    }

}