package com.loadburn.heron.transport;

import com.google.common.io.ByteStreams;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Singleton
class ByteArrayTransport extends Raw {

    @SuppressWarnings("unchecked")
    public <T> T in(InputStream in, Class<T> type) throws IOException {
        assert type == byte[].class;
        return (T) ByteStreams.toByteArray(in);
    }

    @Override
    public <T> T in(InputStream in, TypeLiteral<T> type) throws IOException {
        assert type.getType() == byte[].class;
        return (T) ByteStreams.toByteArray(in);
    }

    public <T> void out(OutputStream out, Class<T> type, T data) throws IOException {
        assert data instanceof byte[];
        out.write((byte[]) data);
    }
}