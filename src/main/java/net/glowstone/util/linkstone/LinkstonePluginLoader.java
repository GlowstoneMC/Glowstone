package net.glowstone.util.linkstone;

import net.glowstone.linkstone.runtime.LinkstoneRuntimeData;
import net.glowstone.linkstone.runtime.boxing.BoxPatchVisitor;
import net.glowstone.linkstone.runtime.direct.DirectFieldAccessReplaceVisitor;
import net.glowstone.linkstone.runtime.inithook.ClassInitInvokeVisitor;
import net.glowstone.linkstone.runtime.reflectionredirect.field.FieldAccessorUtility;
import net.glowstone.linkstone.runtime.reflectionredirect.method.MethodAccessorUtility;
import net.glowstone.linkstone.runtime.reflectionreplace.ReflectionReplaceVisitor;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;

public class LinkstonePluginLoader extends JavaPluginLoader {
    /**
     * Bukkit will invoke this constructor via reflection.
     * Its signature should therefore not be changed!
     *
     * @param instance the server instance
     */
    public LinkstonePluginLoader(Server instance) {
        super(instance);
        LinkstoneRuntimeData.setPluginClassLoader(new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                return loadClass(name);
            }
        });
    }

    @Override
    protected PluginClassLoader newPluginLoader(JavaPluginLoader loader, ClassLoader parent, PluginDescriptionFile description, File dataFolder, File file, ClassLoader libraryLoader) throws Exception {
        return new PluginClassLoader(loader, parent, description, dataFolder, file, libraryLoader) {
            @Override
            protected byte[] transformBytecode(byte[] bytecode) {
                if (LinkstoneRuntimeData.getFields().isEmpty()
                        && LinkstoneRuntimeData.getBoxes().isEmpty()) {
                    // There are no plugins installed that use a @LField or @LBox annotation
                    // so there's no need for runtime support
                    return bytecode;
                }
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

                ClassVisitor cv = cw;
                cv = new DirectFieldAccessReplaceVisitor(LinkstoneRuntimeData.getFields(), cv);
                if (!FieldAccessorUtility.isSupported() || !MethodAccessorUtility.isSupported()) {
                    cv = new ReflectionReplaceVisitor(cv);
                }
                cv = new ClassInitInvokeVisitor(cv);
                cv = new BoxPatchVisitor(LinkstoneRuntimeData.getBoxes(), cv);

                new ClassReader(bytecode).accept(cv, 0);
                return cw.toByteArray();
            }
        };
    }
}
