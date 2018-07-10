package net.glowstone.util.linkstone;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.glowstone.linkstone.runtime.LinkstoneRuntimeData;
import net.glowstone.linkstone.runtime.boxing.BoxPatchVisitor;
import net.glowstone.linkstone.runtime.direct.DirectFieldAccessReplaceVisitor;
import net.glowstone.linkstone.runtime.inithook.ClassInitInvokeVisitor;
import net.glowstone.linkstone.runtime.reflectionredirect.FieldRedirectUtil;
import net.glowstone.linkstone.runtime.reflectionreplace.ReflectFieldAccessReplaceVisitor;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

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
                return getClassByName(name);
            }
        });
    }

    @Override
    protected PluginClassLoader newPluginLoader(JavaPluginLoader loader, ClassLoader parent,
            PluginDescriptionFile description, File dataFolder, File file) throws Exception {

        return new PluginClassLoader(loader, parent, description, dataFolder, file) {
            @Override
            protected byte[] transformBytecode(byte[] bytecode) {
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

                ClassVisitor cv = cw;
                cv = new DirectFieldAccessReplaceVisitor(LinkstoneRuntimeData.getFields(), cv);
                if (!FieldRedirectUtil.isSupported()) {
                    cv = new ReflectFieldAccessReplaceVisitor(cv);
                }
                cv = new ClassInitInvokeVisitor(cv);
                cv = new BoxPatchVisitor(LinkstoneRuntimeData.getBoxes(), cv);

                new ClassReader(bytecode).accept(cv, 0);
                return cw.toByteArray();
            }
        };
    }
}
