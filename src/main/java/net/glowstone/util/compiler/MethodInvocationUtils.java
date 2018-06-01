package net.glowstone.util.compiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MethodInvocationUtils {

    /**
     * Invokes a specified static method belonging to a class.
     *
     * @param c The class.
     * @param methodName The method to find in the class.
     * @param args The arguments of the method.
     * @return The result of invoking the method.
     */
    public static Object invokeStaticMethod(Class<?> c, String methodName, Object... args) {
        Method m = findFirstMatchingStaticMethod(c, methodName, args);
        if (m == null) {
            throw new RuntimeException("Could not find method " + methodName);
        }
        try {
            return m.invoke(null, args);
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException
            | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method findFirstMatchingStaticMethod(Class<?> c, String methodName,
        Object... args) {
        Method[] methods = c.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals(methodName) && Modifier.isStatic(m.getModifiers())) {
                Class<?>[] parameterTypes = m.getParameterTypes();
                if (areAssignable(parameterTypes, args)) {
                    return m;
                }
            }
        }
        return null;
    }

    private static boolean areAssignable(Class<?>[] types, Object... args) {
        if (types.length != args.length) {
            return false;
        }
        for (int i = 0; i < types.length; i++) {
            Object arg = args[i];
            Class<?> type = types[i];
            if (arg != null && !type.isAssignableFrom(arg.getClass())) {
                return false;
            }
        }
        return true;
    }
}
