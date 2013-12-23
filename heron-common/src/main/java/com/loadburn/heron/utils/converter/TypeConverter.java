package com.loadburn.heron.utils.converter;

import com.google.inject.ImplementedBy;

import java.lang.reflect.Type;


/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-26
 */
@ImplementedBy(TypeConverterImpl.class)
public interface TypeConverter {
    <T> T convert(Object source, Type type);
}
