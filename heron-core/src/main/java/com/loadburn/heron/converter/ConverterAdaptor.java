package com.loadburn.heron.converter;

import com.loadburn.heron.utils.converter.Converter;

/**
 * @param <S>
 * @param <T>
 */
abstract class ConverterAdaptor<S, T> implements Converter<S, T> {
    @Override
    public S from(T target) {
        return null;
    }
}