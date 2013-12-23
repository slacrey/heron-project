package com.loadburn.heron.transport;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.inject.TypeLiteral;

import java.io.*;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-29
 */
public class StandardTextTransport extends Text {

    @Override
    public <T> T in(InputStream in, Class<T> type) throws IOException {
        return type.cast(CharStreams.toString(new InputStreamReader(in)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T in(InputStream in, TypeLiteral<T> type) throws IOException {
        return (T) CharStreams.toString(new InputStreamReader(in));
    }

    @Override
    public <T> void out(OutputStream out, Class<T> type, T data) throws IOException {
        try {
            ByteStreams.copy(new ByteArrayInputStream(data.toString().getBytes()), out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
