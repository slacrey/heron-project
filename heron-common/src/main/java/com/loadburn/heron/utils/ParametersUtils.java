package com.loadburn.heron.utils;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import java.util.Collection;

public class ParametersUtils {

    public static Multimap<String, String> readMatrix(String uri) {
        ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();
        String[] pieces = uri.split("[/]+");
        for (String piece : pieces) {
            String[] pairs = piece.split("[;]+");

            for (String pair : pairs) {
                String[] singlePair = pair.split("[=]+");
                if (singlePair.length > 1) {
                    builder.put(singlePair[0], singlePair[1]);
                }
            }
        }

        return builder.build();
    }

    public static String singleMatrixParam(String name, Collection<String> values) {
        if (values.size() > 1) {
            throw new IllegalStateException("This matrix parameter has multiple values, "
                    + name + "=" + values);
        }
        return values.isEmpty() ? null : Iterables.getOnlyElement(values);
    }
}
