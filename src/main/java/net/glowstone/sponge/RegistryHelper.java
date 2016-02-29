package net.glowstone.sponge;
/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import net.glowstone.GlowServer;
import org.lwjgl.Sys;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistryHelper {

    private static Field modifiersField = null;

    static {
        try {
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final Logger logger = GlowServer.logger;

    public static boolean mapFields(Class<?> apiClass, Map<String, ?> mapping) {
        return mapFields(apiClass, fieldName -> mapping.get(fieldName.toLowerCase()));
    }

    public static boolean mapFieldsIgnoreWarning(Class<?> apiClass, Map<String, ?> mapping) {
        return mapFields(apiClass, fieldname -> mapping.get(fieldname.toLowerCase()), true);
    }

    public static boolean mapFields(Class<?> apiClass, Function<String, ?> mapFunction) {
        return mapFields(apiClass, mapFunction, false);
    }

    public static boolean mapFields(Class<?> apiClass, Function<String, ?> mapFunction, boolean ignore) {
        boolean mappingSuccess = true;
        for (Field f : apiClass.getDeclaredFields()) {
            try {

                Object value = mapFunction.apply(f.getName());
                if (value == null && !ignore) {
                    logger.log(Level.WARNING, "Skipping " + f.getDeclaringClass().getName() + "." + f.getName());
                    continue;
                }

                if (Modifier.isFinal(f.getModifiers())) {
                    modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                }

                f.set(null, value);
            } catch (Exception e) {
                if (!ignore) {
                    logger.log(Level.WARNING, "Error while mapping " + f.getDeclaringClass().getName() + "." + f.getName(), e);
                }
                mappingSuccess = false;
            }
        }
        return mappingSuccess;
    }

    public static boolean setFactory(Class<?> apiClass, Object factory) {
        try {
            apiClass.getDeclaredField("factory").set(null, factory);
            return true;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while setting factory on " + apiClass.toString(), e);
            return false;
        }
    }

    public static void setFinalStatic(Class<?> clazz, String fieldName, Object newValue) {
        try {
            Field field = clazz.getDeclaredField(fieldName);

            if (Modifier.isFinal(field.getModifiers())) {
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            }

            field.set(null, newValue);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while setting field " + clazz.getName() + "." + fieldName, e);
        }
    }
}