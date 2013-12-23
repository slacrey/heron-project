package com.loadburn.heron.converter;

import com.loadburn.heron.utils.converter.Converter;

import java.util.Arrays;
import java.util.List;

public class SingletonListConverter implements Converter<Object, List<?>> {

    @Override
    public List<?> to(Object source) {
        return Arrays.asList(source);
    }

    @Override
    public Object from(List<?> target) {
        return target.get(0);
    }
}
