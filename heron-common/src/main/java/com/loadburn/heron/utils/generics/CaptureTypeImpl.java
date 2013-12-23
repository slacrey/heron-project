package com.loadburn.heron.utils.generics;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;

class CaptureTypeImpl implements CaptureType {
    private final WildcardType wildcard;
    private final TypeVariable<?> variable;
    private final Type[] lowerBounds;
    private Type[] upperBounds;

    public CaptureTypeImpl(WildcardType wildcard, TypeVariable<?> variable) {
        this.wildcard = wildcard;
        this.variable = variable;
        this.lowerBounds = wildcard.getLowerBounds();
    }

    void init(VariableMap variableMap) {
        ArrayList<Type> upperBoundsList = new ArrayList<Type>();
        upperBoundsList.addAll(Arrays.asList(variableMap.map(variable.getBounds())));
        upperBoundsList.addAll(Arrays.asList(wildcard.getUpperBounds()));
        upperBounds = new Type[upperBoundsList.size()];
        upperBoundsList.toArray(upperBounds);
    }

    public Type[] getLowerBounds() {
        return lowerBounds.clone();
    }

    public Type[] getUpperBounds() {
        assert upperBounds != null;
        return upperBounds.clone();
    }

    @Override
    public String toString() {
        return "capture of " + wildcard;
    }
}