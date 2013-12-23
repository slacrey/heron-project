package com.loadburn.heron.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-29
 */
@Singleton
class JacksonJsonTransport extends Json {

    private final ObjectMapper objectMapper;

    @Inject
    public JacksonJsonTransport(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public <T> T in(InputStream in, Class<T> type) throws IOException {
        return objectMapper.readValue(in, type);
    }

    @Override
    public <T> T in(InputStream in, TypeLiteral<T> type) throws IOException {
        return (T)objectMapper.readValue(in, TypeFactory.defaultInstance().constructType(type.getType()));
    }

    public <T> void out(OutputStream out, Class<T> type, T data) {
        try {
            objectMapper.writeValue(out, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
