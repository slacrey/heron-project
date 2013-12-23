package com.loadburn.heron.utils.generics;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

/**
 * 泛型反射类，获取上下边界
 */
public class WildcardTypeImpl implements WildcardType {

    private final Type[] upperBounds;
    private final Type[] lowerBounds;

    public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
        if (upperBounds.length == 0)
            throw new IllegalArgumentException(
                    "There must be path least one upper bound. For an unbound wildcard, the upper bound must be Object");
        this.upperBounds = upperBounds;
        this.lowerBounds = lowerBounds;
    }

    /**
     *获取上边界
     * @return
     */
    public Type[] getUpperBounds() {
        return upperBounds;
    }

    /**
     * 获取下边界
     * @return
     */
    public Type[] getLowerBounds() {
        return lowerBounds;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WildcardType))
            return false;
        WildcardType other = (WildcardType) obj;
        return Arrays.equals(lowerBounds, other.getLowerBounds())
                && Arrays.equals(upperBounds, other.getUpperBounds());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(lowerBounds) ^ Arrays.hashCode(upperBounds);
    }

    @Override
    public String toString() {
        if (lowerBounds.length > 0) {
            return "? super " + GenericsUtils.getTypeName(lowerBounds[0]);
        } else if (upperBounds[0] == Object.class) {
            return "?";
        } else {
            return "? extends " + GenericsUtils.getTypeName(upperBounds[0]);
        }
    }
}