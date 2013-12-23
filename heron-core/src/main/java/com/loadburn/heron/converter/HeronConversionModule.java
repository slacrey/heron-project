package com.loadburn.heron.converter;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.loadburn.heron.utils.converter.Converter;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-26
 */
public class HeronConversionModule extends AbstractModule {
    @Override
    protected void configure() {
        converters = Multibinder.newSetBinder(binder(), Converter.class);
        ConverterBuilder.createConverterMultibinder(converters);
    }

    @SuppressWarnings("rawtypes")
    private Multibinder<Converter> converters;

    public final void converter(Converter<?, ?> converter) {
        Preconditions.checkArgument(null != converter, "converter不能为空");
        converters.addBinding().toInstance(converter);
    }

    public final void converter(Class<? extends Converter<?, ?>> clazz) {
        converters.addBinding().to(clazz);
    }
}
