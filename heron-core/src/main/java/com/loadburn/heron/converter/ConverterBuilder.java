package com.loadburn.heron.converter;

import com.google.inject.multibindings.Multibinder;
import com.loadburn.heron.utils.converter.Converter;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-26
 */
public class ConverterBuilder {

    public static Multibinder<Converter> createConverterMultibinder(Multibinder<Converter> converters) {

        // register the default converters after user converters
        converters.addBinding().to(ObjectToStringConverter.class);

        // allow single request parameters to be converted to List<String>
        converters.addBinding().to(SingletonListConverter.class);

        for (Converter<?, ?> converter : StringToPrimitiveConverters.converters()) {
            converters.addBinding().toInstance(converter);
        }

        for (Converter<?, ?> converter : NumberConverters.converters()) {
            converters.addBinding().toInstance(converter);
        }

        for (Class<? extends Converter<?, ?>> converterClass : DateConverters.converters()) {
            converters.addBinding().to(converterClass);
        }

        return converters;

    }
}
