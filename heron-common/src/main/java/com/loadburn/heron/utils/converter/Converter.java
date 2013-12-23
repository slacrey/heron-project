package com.loadburn.heron.utils.converter;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-26
 */
public interface Converter<S, T> {
    T to(S source);

    S from(T target);
}
