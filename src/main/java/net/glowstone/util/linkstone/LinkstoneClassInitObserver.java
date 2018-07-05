package net.glowstone.util.linkstone;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.aki.linkstone.runtime.inithook.ClassInitHook;
import me.aki.linkstone.runtime.inithook.ClassInitInvokeVisitor;
import me.aki.linkstone.runtime.reflectionredirect.FieldRedirectUtil;

/**
 * Utility that redirects reflective uses of annotated fields to their getters and setters.
 *
 * @see FieldRedirectUtil
 * @see ClassInitHook
 * @see ClassInitInvokeVisitor
 */
public class LinkstoneClassInitObserver implements ClassInitHook.Observer {
    private final FieldRedirectUtil fieldRedirectUtil;

    /**
     * Create a new observer instance.
     */
    public LinkstoneClassInitObserver() {
        try {
            fieldRedirectUtil = FieldRedirectUtil.isSupported()
                    ? new FieldRedirectUtil() : null;
        } catch (Throwable t) {
            throw new IllegalStateException("Could not initalize FieldRedirectUtil");
        }
    }

    @Override
    public void onInit(Class<?> clazz) {
        if (fieldRedirectUtil == null) {
            return;
        }

        try {
            for (Field field : getAllFields(clazz)) {
                me.aki.linkstone.annotations.Field[] annotations =
                        field.getAnnotationsByType(me.aki.linkstone.annotations.Field.class);

                if (annotations.length > 0) {
                    try {
                        fieldRedirectUtil.redirectField(field);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Field> getAllFields(Class<?> clazz)
            throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, NoSuchFieldException {
        List<Field> fields = new ArrayList<>();

        // initializes reflectionData
        clazz.getDeclaredFields();

        Method reflectionDataMethod = Class.class.getDeclaredMethod("reflectionData");
        reflectionDataMethod.setAccessible(true);
        Object reflectionData = reflectionDataMethod.invoke(clazz);

        Field[] fieldFields = new Field[] {
                reflectionData.getClass().getDeclaredField("declaredFields"),
                reflectionData.getClass().getDeclaredField("publicFields"),
                reflectionData.getClass().getDeclaredField("declaredPublicFields")
        };

        for (Field f : fieldFields) {
            f.setAccessible(true);
            Field[] fieldArray = (Field[]) f.get(reflectionData);
            if (fieldArray != null) {
                fields.addAll(Arrays.asList(fieldArray));
            }
        }

        return fields;
    }
}
