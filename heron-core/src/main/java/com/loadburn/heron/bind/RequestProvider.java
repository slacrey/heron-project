package com.loadburn.heron.bind;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteStreams;
import com.google.inject.*;
import com.loadburn.heron.transport.Transport;
import com.loadburn.heron.utils.ParametersUtils;
import com.loadburn.heron.validation.HeronValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-25
 */
@Singleton
public class RequestProvider implements Provider<Request<String>> {

    private final Provider<HttpServletRequest> servletRequest;
    private final Injector injector;
    private final HeronValidator validator;

    @Inject
    RequestProvider(Provider<HttpServletRequest> servletRequest, Injector injector, HeronValidator validator) {
        this.servletRequest = servletRequest;
        this.injector = injector;
        this.validator = validator;
    }

    @Override
    public Request<String> get() {
        return new Request<String>() {

            HttpServletRequest servletRequest = RequestProvider.this.servletRequest.get();
            Multimap<String, String> matrix;
            Multimap<String, String> headers;
            Multimap<String, String> params;
            String method;


            @SuppressWarnings("unchecked")
            @Override
            public <E> RequestRead read(final Class<E> type) {
                return new RequestRead<E>() {
                    E memo;

                    @Override
                    public E as(Class<? extends Transport> transport) {
                        try {
                            if (null == memo) {
                                memo = injector.getInstance(transport).in(servletRequest.getInputStream(),
                                        type);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("从servlet请求中无法获取输入流" +
                                    " (它可能在其他地方使用或是已经关闭?). Error:\n" + e.getMessage(), e);
                        }

                        return memo;
                    }
                };
            }

            @Override
            public <E> RequestRead<E> read(final TypeLiteral<E> type) {
                return new RequestRead<E>() {
                    E memo;

                    @Override
                    public E as(Class<? extends Transport> transport) {
                        try {
                            if (null == memo) {
                                memo = injector.getInstance(transport).in(servletRequest.getInputStream(),
                                        type);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("从servlet请求中无法获取输入流" +
                                    " (它可能在其他地方使用或是已经关闭?). Error:\n" + e.getMessage(), e);
                        }

                        return memo;
                    }
                };
            }

            @Override
            public void readTo(OutputStream out) throws IOException {
                ByteStreams.copy(servletRequest.getInputStream(), out);
            }

            @Override
            public Multimap<String, String> headers() {
                if (null == headers) {
                    readHeaders();
                }
                return headers;
            }

            @Override
            public Multimap<String, String> matrix() {
                if (null == matrix) {
                    this.matrix = ParametersUtils.readMatrix(servletRequest.getRequestURI());
                }
                return matrix;
            }

            @Override
            public String matrixParam(String name) {
                if (null == matrix) {
                    this.matrix = ParametersUtils.readMatrix(servletRequest.getRequestURI());
                }
                return ParametersUtils.singleMatrixParam(name, matrix.get(name));
            }

            @Override
            public String header(String name) {
                return servletRequest.getHeader(name);
            }

            @Override
            public String uri() {
                return servletRequest.getRequestURI();
            }

            @Override
            public String path() {
                return servletRequest.getRequestURI().substring(servletRequest.getContextPath().length());
            }

            @Override
            public String completePath() {
                String completePath=  servletRequest.getScheme() + "://" +servletRequest.getServerName() + ":"
                        + servletRequest.getServerPort() + context();
                return completePath;
            }

            @Override
            public String context() {
                return servletRequest.getContextPath();
            }

            @Override
            public String method() {
                if (method == null) {
                    method = servletRequest.getMethod();
                }
                return method;
            }

            @Override
            public void validate(Object object) {
                Set<? extends ConstraintViolation<?>> cvs = validator.validate(object);
                if ((cvs != null) && (!cvs.isEmpty())) {
                    throw new ValidationException(new ConstraintViolationException(cvs));
                }
            }

            @Override
            public String param(String name) {
                return servletRequest.getParameter(name);
            }

            @Override
            public Multimap<String, String> params() {
                if (null == params) {
                    readParams();
                }
                return params;
            }

            private void readHeaders() {
                ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();

                @SuppressWarnings("unchecked")
                Enumeration<String> headerNames = servletRequest.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String header = headerNames.nextElement();

                    @SuppressWarnings("unchecked")
                    Enumeration<String> values = servletRequest.getHeaders(header);
                    while (values.hasMoreElements()) {
                        builder.put(header, values.nextElement());
                    }
                }

                this.headers = builder.build();
            }

            private void readParams() {
                ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();

                @SuppressWarnings("unchecked")
                Map<String, String[]> parameterMap = servletRequest.getParameterMap();
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    builder.putAll(entry.getKey(), entry.getValue());
                }

                this.params = builder.build();
            }

        };
    }


}
