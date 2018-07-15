package net.glowstone.util.linkstone;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.glowstone.linkstone.annotations.LBox;
import net.glowstone.linkstone.annotations.LField;
import net.glowstone.linkstone.runtime.inithook.ClassInitHook;
import net.glowstone.linkstone.runtime.reflectionredirect.BoxingMethodAccessor;
import net.glowstone.linkstone.runtime.reflectionredirect.FieldRedirectUtil;
import net.glowstone.linkstone.runtime.reflectionredirect.LMethodAccessor;
import net.glowstone.linkstone.runtime.reflectionredirect.MethodAccessorUtility;

/**
 * Utility that redirects reflective uses of annotated fields to their getters and setters.
 */
public class LinkstoneClassInitObserver implements ClassInitHook.Observer {
    private final FieldRedirectUtil fieldRedirectUtil;
    private final MethodAccessorUtility methodAccessorUtil;

    /**
     * Create a new observer instance.
     */
    public LinkstoneClassInitObserver() {
        try {
            fieldRedirectUtil = FieldRedirectUtil.isSupported()
                    ? new FieldRedirectUtil() : null;
        } catch (Exception t) {
            throw new IllegalStateException("Could not initalize FieldRedirectUtil");
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
        if (fieldRedirectUtil == null) {
            return;
        }

        for (Field field : getAllFields(clazz)) {
            LField[] annotations = field.getAnnotationsByType(LField.class);

            if (annotations.length > 0) {
                try {
                    fieldRedirectUtil.redirectField(field);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void hijackMethods(Class<?> clazz) throws ReflectiveOperationException {
        if (methodAccessorUtil == null || clazz.getAnnotation(LBox.class) == null) {
            return;
        }

        for (Method method : getAllMethods(clazz)) {
            initializeInternalMethodAccessorField(method);

            LMethodAccessor accessor = methodAccessorUtil.getAccessor(method);
            accessor = new BoxingMethodAccessor(method, accessor);
            methodAccessorUtil.setAccessor(method, accessor);
        }
    }

    private void initializeInternalMethodAccessorField(Method method) throws ReflectiveOperationException {
        Method m = Method.class.getDeclaredMethod("acquireMethodAccessor");
        m.setAccessible(true);
        m.invoke(method);
    }

    /**
     * Methods as gotten from {@link Class} api are copies.
     * This methods returns all internal methods.
     *
     * @param clazz whose methods to get
     * @return all internal methods
     */
    private List<Method> getAllMethods(Class<?> clazz) throws ReflectiveOperationException {
        // initializes reflectionData
        clazz.getMethods();
        clazz.getDeclaredMethods();

        Object reflectionData = getReflectionData(clazz);

        Field[] methodFields = new Field[] {
                reflectionData.getClass().getDeclaredField("declaredMethods"),
                reflectionData.getClass().getDeclaredField("publicMethods"),
                reflectionData.getClass().getDeclaredField("declaredPublicMethods")
        };

        List<Method> methods = new ArrayList<>();
        for (Field f : methodFields) {
            f.setAccessible(true);
            Method[] methodArray = (Method[]) f.get(reflectionData);
            if (methodArray != null) {
                methods.addAll(Arrays.asList(methodArray));
            }
        }
        return methods;
    }

    /**
     * Fields gotten from the {@link Class} api are copies.
     * This method returns all original fields.
     *
     * @param clazz whose fields to get
     * @return al internal fields
     */
    private List<Field> getAllFields(Class<?> clazz) throws ReflectiveOperationException {
        // initializes reflectionData
        clazz.getFields();
        clazz.getDeclaredFields();

        Object reflectionData = getReflectionData(clazz);

        Field[] fieldFields = new Field[] {
                reflectionData.getClass().getDeclaredField("declaredFields"),
                reflectionData.getClass().getDeclaredField("publicFields"),
                reflectionData.getClass().getDeclaredField("declaredPublicFields")
        };

        List<Field> fields = new ArrayList<>();
        for (Field f : fieldFields) {
            f.setAccessible(true);
            Field[] fieldArray = (Field[]) f.get(reflectionData);
            if (fieldArray != null) {
                fields.addAll(Arrays.asList(fieldArray));
            }
        }
        return fields;
    }

    private Object getReflectionData(Class<?> clazz) throws ReflectiveOperationException {
        Method reflectionDataMethod = Class.class.getDeclaredMethod("reflectionData");
        reflectionDataMethod.setAccessible(true);
        return reflectionDataMethod.invoke(clazz);
    }
}
