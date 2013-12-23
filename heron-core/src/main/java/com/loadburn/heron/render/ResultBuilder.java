package com.loadburn.heron.render;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.loadburn.heron.complier.Templates;
import com.loadburn.heron.transport.Text;
import com.loadburn.heron.transport.Transport;
import com.loadburn.heron.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-29
 */
public class ResultBuilder<E> extends Result<E> {

    E entity;
    int status = HttpServletResponse.SC_OK;
    String contentType;
    String redirectUri;
    Map<String, String> headers = Maps.newHashMap();
    Key<? extends Transport> transport = Key.get(Text.class);
    Class<?> templateKey;

    public ResultBuilder(E entity) {
        this.entity = entity;
    }

    @Override
    public Result<E> seeOther(String uri) {
        redirectUri = uri;
        status = HttpServletResponse.SC_MOVED_PERMANENTLY;
        return this;
    }

    @Override
    public Result<E> seeOther(String uri, int statusCode) {
        Preconditions.checkArgument(statusCode >= 300 && statusCode < 400,
                "跳转http状态必须在300-399之间");
        redirectUri = uri;
        status = statusCode;
        return this;
    }

    @Override
    public Result<E> type(String mediaType) {
        StringUtils.nonEmpty(mediaType, "介质类型(mediaType)不能为null或empty");
        this.contentType = mediaType;
        return this;
    }

    @Override
    public Result<E> headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    @Override
    public Result<E> notFound() {
        status = HttpServletResponse.SC_NOT_FOUND;
        return this;
    }

    @Override
    public Result<E> unauthorized() {
        status = HttpServletResponse.SC_UNAUTHORIZED;
        return this;
    }

    @Override
    public Result<E> as(Class<? extends Transport> transport) {
        Preconditions.checkArgument(null != transport, "继承Transport的类不能为null");
        this.transport = Key.get(transport);
        return this;
    }

    @Override
    public Result<E> as(Key<? extends Transport> transport) {
        Preconditions.checkArgument(null != transport, "继承Transport的类不能为null");
        this.transport = transport;
        return this;
    }

    @Override
    public Result<E> redirect(String uri) {
        StringUtils.nonEmpty(uri, "跳转URI不能为null或empty");
        this.redirectUri = uri;
        status = HttpServletResponse.SC_MOVED_TEMPORARILY;
        return this;
    }

    @Override
    public Result<E> forbidden() {
        status = HttpServletResponse.SC_FORBIDDEN;
        return this;
    }

    @Override
    public Result<E> noContent() {
        status = HttpServletResponse.SC_NO_CONTENT;
        return this;
    }

    @Override
    public Result<E> error() {
        status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        return this;
    }

    @Override
    public Result<E> badRequest() {
        status = HttpServletResponse.SC_BAD_REQUEST;
        return this;
    }

    @Override
    public Result<E> template(Class<?> templateKey) {
        this.templateKey = templateKey;
        return this;
    }

    @Override
    public Result<E> status(int code) {
        status = code;
        return this;
    }

    @Override
    public Result<E> ok() {
        status = HttpServletResponse.SC_OK;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void render(Injector injector, HttpServletResponse response) throws IOException {
        if (NO_RESULT == this) {
            injector.getInstance(HttpServletRequest.class).setAttribute(NO_RESPOND_ATTR, Boolean.TRUE);
            return;
        }

        Transport transport = injector.getInstance(this.transport);

        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                response.setHeader(header.getKey(), header.getValue());
            }
        }

        if (response.getContentType() == null) {
            if (null == contentType) {
                response.setContentType(transport.contentType());
            } else {
                response.setContentType(contentType);
            }
        }

        if (null != redirectUri) {
            response.sendRedirect(redirectUri);
            response.setStatus(status);
            return;
        }

        response.setStatus(status);

        if (null != templateKey) {
            response.getWriter().write(injector.getInstance(Templates.class).render(templateKey, entity));
        } else if (null != entity) {
            if (entity instanceof InputStream) {
                InputStream inputStream = (InputStream) entity;
                try {
                    ByteStreams.copy(inputStream, response.getOutputStream());
                } finally {
                    inputStream.close();
                }
            } else {
                transport.out(response.getOutputStream(), (Class<E>) entity.getClass(), entity);
            }
        }
    }
}
