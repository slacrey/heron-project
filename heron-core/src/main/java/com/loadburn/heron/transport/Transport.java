package com.loadburn.heron.transport;

import com.google.inject.TypeLiteral;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Transport {

    <T> T in(InputStream in, Class<T> type) throws IOException;

    <T> T in(InputStream in, TypeLiteral<T> type) throws IOException;

    <T> void out(OutputStream out, Class<T> type, T data) throws IOException;

    @SuppressWarnings("UnusedDeclaration")
    String contentType();

}