package net.glowstone.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;

/**
 * Various utilities for annotation reflection.
 */
public class AnnotationUtil {

    /**
     * Checks whether the given class has the given annotation.
     *
     * @param annotation the annotation class
     * @param clazz      the class
     * @return whether the class has the given annotation
     */
    public static boolean hasClassAnnotation(Class<? extends Annotation> annotation,
                                             Class<?> clazz) {
        checkNotNull(annotation);
        checkNotNull(clazz);
        return clazz.isAnnotationPresent(annotation);
    }

    /**
     * Gets the annotation instance for the given class.
     *
     * @param annotation the annotation class
     * @param clazz      the target class
     * @param <T>        the type of the annotation
     * @return the annotation instance, or null if the annotation is not present
     */
    public static <T extends Annotation> T getClassAnnotation(Class<T> annotation,
                                                              Class<?> clazz) {
        if (!hasClassAnnotation(annotation, clazz)) {
            return null;
        }
        return clazz.getAnnotation(annotation);
    }
}
