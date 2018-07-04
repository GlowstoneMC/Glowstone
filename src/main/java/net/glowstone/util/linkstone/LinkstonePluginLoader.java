package net.glowstone.util.linkstone;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.aki.linkstone.runtime.FieldAccessBus;
import me.aki.linkstone.runtime.OnClassInitInvokeVisitor;
import me.aki.linkstone.runtime.direct.DirectFieldAccessReplaceVisitor;
import me.aki.linkstone.runtime.reflectionredirect.FieldRedirectUtil;
import me.aki.linkstone.runtime.reflectionreplace.ReflectFieldAccessReplaceVisitor;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class LinkstonePluginLoader extends JavaPluginLoader {
    private static final Method METHOD_ON_CLASS_LOAD;
    private static final FieldRedirectUtil FIELD_REDIRECT_UTIL;

    static {
        try {
            METHOD_ON_CLASS_LOAD = LinkstonePluginLoader.class
                    .getDeclaredMethod("onClassLoad", Class.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Could not find onClassLoad method");
        }

        try {
            FIELD_REDIRECT_UTIL = FieldRedirectUtil.isSupported() ?
                    new FieldRedirectUtil() : null;
        } catch (Throwable t) {
            throw new IllegalStateException("Could not initalize FieldRedirectUtil");
        }
    }

    /**
     * This method will be called whenever a class of a plugin is initialized.
     * It will try to use the {@link FieldRedirectUtil} on all annotated fields.
     *
     * @param loadedClass plugin class that was just initialized
     * @see LinkstonePluginLoader#METHOD_ON_CLASS_LOAD
     * @see OnClassInitInvokeVisitor
     */
    public static void onClassLoad(Class<?> loadedClass) {
        if (FIELD_REDIRECT_UTIL == null) {
            return;
        }

        for (Field field : loadedClass.getDeclaredFields()) {
            me.aki.linkstone.annotations.Field[] annotations =
                    field.getAnnotationsByType(me.aki.linkstone.annotations.Field.class);

            if (annotations.length > 0) {
                try {
                    FIELD_REDIRECT_UTIL.redirectField(field);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Bukkit will invoke this constructor via reflection.
     * Its signature should therefore not be changed!
     *
     * @param instance the server instance
     */
    public LinkstonePluginLoader(Server instance) {
        super(instance);
    }

    @Override
    protected PluginClassLoader newPluginLoader(JavaPluginLoader loader, ClassLoader parent,
            PluginDescriptionFile description, File dataFolder, File file) throws Exception {

        return new PluginClassLoader(loader, parent, description, dataFolder, file) {
            @Override
            protected byte[] transformBytecode(byte[] bytecode) {
                ClassWriter cw = new ClassWriter(0);

                ClassVisitor cv = cw;
                cv = new DirectFieldAccessReplaceVisitor(FieldAccessBus.getFields(), cv);
                if (!FieldRedirectUtil.isSupported()) {
                    cv = new ReflectFieldAccessReplaceVisitor(cv);
                }
                cv = new OnClassInitInvokeVisitor(METHOD_ON_CLASS_LOAD, cv);

                new ClassReader(bytecode).accept(cv, 0);
                return cw.toByteArray();
            }
        };
    }
}
