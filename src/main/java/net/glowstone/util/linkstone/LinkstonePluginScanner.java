package net.glowstone.util.linkstone;

import net.glowstone.linkstone.annotations.LField;
import net.glowstone.linkstone.runtime.Boxes;
import net.glowstone.linkstone.runtime.FieldSet;
import net.glowstone.linkstone.runtime.collect.AnnotatedFieldCollectVisitor;
import net.glowstone.linkstone.runtime.collect.BoxCollectVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LinkstonePluginScanner {
    private final FieldSet fields;
    private final Boxes boxes;

    public LinkstonePluginScanner(final FieldSet fields, final Boxes boxes) {
        this.fields = fields;
        this.boxes = boxes;
    }

    /**
     * Look through a list of plugins jar files and store all
     * fields annotated with a {@link LField} annotation.
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
            cv = new BoxCollectVisitor(this.boxes, cv);

            new ClassReader(zin).accept(cv, ClassReader.SKIP_CODE);
            zin.closeEntry();
        }
        zin.close();
    }
}
