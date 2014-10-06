package net.glowstone.testutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utilities for parameterized tests.
 */
public final class ParameterUtils {

    private ParameterUtils() {
    }

    public static Collection<Object[]> enumCases(Object[] values) {
        List<Object[]> result = new ArrayList<>(values.length);
        for (Object value : values) {
            result.add(new Object[]{value});
        }
        return result;
    }

}
