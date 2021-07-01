package net.glowstone.util.linkstone;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;
import net.glowstone.linkstone.annotations.LBox;
import net.glowstone.linkstone.annotations.LField;
import net.glowstone.linkstone.runtime.inithook.ClassInitHook;
import net.glowstone.linkstone.runtime.reflectionredirect.DynamicClassLoader;
import net.glowstone.linkstone.runtime.reflectionredirect.ReflectionUtil;
import net.glowstone.linkstone.runtime.reflectionredirect.field.BoxingFieldAccessor;
import net.glowstone.linkstone.runtime.reflectionredirect.field.FieldAccessorUtility;
import net.glowstone.linkstone.runtime.reflectionredirect.field.LFieldAccessor;
import net.glowstone.linkstone.runtime.reflectionredirect.field.RedirectFieldAccessorGenerator;
import net.glowstone.linkstone.runtime.reflectionredirect.method.BoxingMethodAccessor;
import net.glowstone.linkstone.runtime.reflectionredirect.method.LMethodAccessor;
import net.glowstone.linkstone.runtime.reflectionredirect.method.MethodAccessorUtility;

/**
 * Utility that redirects reflective uses of annotated fields to their getters and setters.
 */
public class LinkstoneClassInitObserver implements ClassInitHook.Observer {
    private final FieldAccessorUtility fieldAccessorUtil;
    private final MethodAccessorUtility methodAccessorUtil;

    private final Map<ClassLoader, DynamicClassLoader> classLoaders = new WeakHashMap<>();

    /**
     * Create a new observer instance.
     */
    public LinkstoneClassInitObserver() {
        try {
            fieldAccessorUtil = FieldAccessorUtility.isSupported()
                    ? new FieldAccessorUtility() : null;
        } catch (Exception t) {
            throw new IllegalStateException("Could not initialize FieldAccessorUtility");
        }

        try {
            methodAccessorUtil = MethodAccessorUtility.isSupported()
                    ? new MethodAccessorUtility() : null;
        } catch (Exception e) {
            throw new IllegalStateException("Could not initialize MethodAccessorUtility");
        }
    }

    @Override
    public void onInit(Class<?> clazz) {
        try {
            hijackFields(clazz);
            hijackMethods(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hijackFields(Class<?> clazz) throws ReflectiveOperationException {
        if (fieldAccessorUtil == null) {
            return;
        }

        boolean isBox = clazz.getAnnotation(LBox.class) != null;

        for (Field field : ReflectionUtil.getInternalFields(clazz)) {
            LField[] fieldAnnotations = field.getAnnotationsByType(LField.class);
            if (fieldAnnotations.length > 0) {
                LFieldAccessor accessor = newRedirectFieldAccessor(field);
                fieldAccessorUtil.setAccessor(field, accessor);
                fieldAccessorUtil.setOverrideAccessor(field, accessor);
            }

            if (isBox) {
                LFieldAccessor accessor = fieldAccessorUtil.getAccessor(field);
                accessor = new BoxingFieldAccessor(accessor, field);
                fieldAccessorUtil.setAccessor(field, accessor);

                LFieldAccessor overrideAccessor = fieldAccessorUtil.getOverrideAccessor(field);
                overrideAccessor = new BoxingFieldAccessor(overrideAccessor, field);
                fieldAccessorUtil.setOverrideAccessor(field, overrideAccessor);
            }
        }
    }

    private LFieldAccessor newRedirectFieldAccessor(Field field)
            throws ReflectiveOperationException {
        DynamicClassLoader classloader = classLoaders.computeIfAbsent(
                field.getDeclaringClass().getClassLoader(), DynamicClassLoader::new);

        RedirectFieldAccessorGenerator generator = new RedirectFieldAccessorGenerator(field);
        String className = generator.getClassName().replace('/', '.');

        Class<?> accessorClass;
        try {
            accessorClass = Class.forName(className, false, classloader);
        } catch (ClassNotFoundException e) {
            byte[] bytecode = generator.generateAccessor();
            accessorClass = classloader.loadBytecode(className, bytecode);
        }

        return (LFieldAccessor) accessorClass.getDeclaredConstructor().newInstance();
    }

    private void hijackMethods(Class<?> clazz) throws ReflectiveOperationException {
        if (methodAccessorUtil == null || clazz.getAnnotation(LBox.class) == null) {
            return;
        }

        for (Method method : ReflectionUtil.getInternalMethods(clazz)) {
            LMethodAccessor accessor = methodAccessorUtil.getAccessor(method);
            accessor = new BoxingMethodAccessor(method, accessor);
            methodAccessorUtil.setAccessor(method, accessor);
        }
    }
}
