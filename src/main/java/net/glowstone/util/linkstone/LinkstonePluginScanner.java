package net.glowstone.util.linkstone;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import me.aki.linkstone.annotations.Field;
import me.aki.linkstone.runtime.FieldSet;
import me.aki.linkstone.runtime.collect.AnnotatedFieldCollectVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

public class LinkstonePluginScanner {
    private final FieldSet fields;

    public LinkstonePluginScanner(final FieldSet fields) {
        this.fields = fields;
    }

    /**
     * Look through a list of plugins jar files and store all
     * fields annotated with a {@link Field} annotation.
     *
     * @param pluginJars list of plugins jars to be scanned
     */
    public void scanPlugins(List<File> pluginJars) {
        for (File pluginJar : pluginJars) {
            try {
                scanPlugin(pluginJar);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void scanPlugin(File pluginJar) throws IOException {
        ZipInputStream zin = new ZipInputStream(new FileInputStream(pluginJar));
        ZipEntry entry;
        while ((entry = zin.getNextEntry()) != null) {
            if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                continue;
            }

            ClassVisitor cv = new AnnotatedFieldCollectVisitor(this.fields);
            new ClassReader(zin).accept(cv, ClassReader.SKIP_CODE);
            zin.closeEntry();
        }
        zin.close();
    }
}
