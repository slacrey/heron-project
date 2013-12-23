package com.loadburn.heron.transport;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.loadburn.heron.bind.Request;
import com.loadburn.heron.bind.RequestBinder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class UrlEncodedFormTransport extends Form {
    
    private final Request<String> request;
    
    private final RequestBinder<String> binder;
    
    @Inject
    public UrlEncodedFormTransport(Request<String> request, RequestBinder<String> binder) {
        this.request = request;
        this.binder = binder;
    }

    public <T> T in(InputStream in, Class<T> type) throws IOException {
        T t;
        try {
           t = type.newInstance();
           binder.bind(request, t);
           request.validate(t);
        }
        catch (InstantiationException e) {
          throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return t;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T in(InputStream in, TypeLiteral<T> type) throws IOException {
        T t;
        try {
           t = (T) type.getRawType().newInstance();
           binder.bind(request, t);
           request.validate(t);
        }
        catch (InstantiationException e) {
          throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return t;
    }

    public <T> void out(OutputStream out, Class<T> type, T data) {
        throw new IllegalAccessError("You should not write to a form transport.");
    }
    
}