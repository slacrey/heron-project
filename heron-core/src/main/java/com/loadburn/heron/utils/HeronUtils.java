package com.loadburn.heron.utils;

import com.loadburn.heron.annotations.Decoration;

public class HeronUtils {
    public HeronUtils() {
    }

    public static Class<?> decorateClass(final Class<?> extendClassArgument) {
        Class<?> extendClass = extendClassArgument;
        while (extendClass != Object.class) {
            extendClass = extendClass.getSuperclass();
            if (extendClass.isAnnotationPresent(Decoration.class)) {
                decorateClass(extendClass);
            } else if (extendClass.isAnnotationPresent(com.loadburn.heron.annotations.Show.class)) {
                return extendClass;
            }
        }
        throw new IllegalStateException("Could not find super class annotated with @Show on parent of class: " + extendClassArgument);
    }
}