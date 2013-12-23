package com.loadburn.heron.transport;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.loadburn.heron.bind.MultiPartRequest;
import com.loadburn.heron.bind.RequestBinder;
import org.apache.commons.fileupload.FileItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class MultiPartFormTransport extends MultiPartForm {
    
    private final MultiPartRequest multiPartRequest;

    private final RequestBinder<FileItem> binder;
    
    @Inject
    public MultiPartFormTransport(MultiPartRequest multiPartRequest, RequestBinder<FileItem> binder) {
        this.multiPartRequest = multiPartRequest;
        this.binder = binder;
    }

    public <T> T in(InputStream in, Class<T> type) throws IOException {
        T t;
        try {
            t = type.newInstance();
            binder.bind(multiPartRequest, t);
            multiPartRequest.validate(t);
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
            binder.bind(multiPartRequest, t);
            multiPartRequest.validate(t);
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
        throw new IllegalAccessError("不支持输出");
    }

}