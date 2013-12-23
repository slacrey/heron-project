package com.loadburn.heron.bind;

import com.google.common.collect.Multimap;
import com.google.inject.TypeLiteral;
import com.loadburn.heron.transport.Transport;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-25
 */
public interface Request<P> {

    <E> RequestRead<E> read(Class<E> type);

    <E> RequestRead<E> read(TypeLiteral<E> type);

    void readTo(OutputStream out) throws IOException;

    Multimap<String, String> headers();

    Multimap<String, String> matrix();

    String matrixParam(String name);

    String header(String name);

    String uri();

    String path();

    String completePath();

    String context();

    String method();

    void validate(Object obj);

    P param(String name);

    Multimap<String, P> params();

    public static interface RequestRead<E> {
        E as(Class<? extends Transport> transport);
    }

}
