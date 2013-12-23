package com.loadburn.heron.converter;

import com.google.inject.Singleton;

@Singleton
public class ObjectToStringConverter extends ConverterAdaptor<Object, String> {

    @Override
    public String to(Object source) {
        return source.toString();
    }
}
