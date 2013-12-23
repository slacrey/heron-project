package com.loadburn.heron.utils.converter;

import com.google.common.collect.Multimap;
import com.google.inject.ImplementedBy;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-26
 */
@ImplementedBy(TypeConverterImpl.class)
public interface ConverterRegistry {
    void register(Converter<?, ?> converter);

    Multimap<Type, Converter<?, ?>> getConvertersByTarget();

    Multimap<Type, Converter<?, ?>> getConvertersBySource();

    Collection<Converter<?, ?>> converter(Type source, Type target);
}
