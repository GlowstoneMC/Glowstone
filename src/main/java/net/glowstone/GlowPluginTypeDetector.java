package net.glowstone;

import com.google.common.io.PatternFilenameFilter;
import net.glowstone.i18n.ConsoleMessages;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class GlowPluginTypeDetector {

    public List<File> bukkitPlugins = new ArrayList<>();
    public List<File> spongePlugins = new ArrayList<>();
    public List<File> canaryPlugins = new ArrayList<>();
    public List<File> forgefPlugins = new ArrayList<>();
    public List<File> forgenPlugins = new ArrayList<>();
    public List<File> unrecognizedPlugins = new ArrayList<>();

    private File directory;

    public GlowPluginTypeDetector(File directory) {
        this.directory = directory;
    }

    /**
     * Scans all jars in the plugin directory for their types.
     */
    public void scan() {
        ConsoleMessages.Info.Plugin.SCANNING.log();
        File[] files = directory.listFiles(new PatternFilenameFilter(".+\\.jar")); // NON-NLS
        if (files == null || files.length == 0) {
            return;
        }

        for (File file : files) {
            scanFile(file);
        }

        ConsoleMessages.Info.Plugin.COUNTS.log(
            bukkitPlugins.size(),
            spongePlugins.size(),
            forgefPlugins.size() + forgenPlugins.size(),
            canaryPlugins.size(),
            unrecognizedPlugins.size(),
            files.length
        );

        if (!unrecognizedPlugins.isEmpty()) {
            for (File file : unrecognizedPlugins) {
                ConsoleMessages.Warn.Plugin.UNRECOGNIZED.log(file.getPath());
            }
        }
    }

    private void scanFile(File file) {
        boolean isBukkit = false;
        boolean isSponge = false;
        boolean isCanary = false;
        boolean isForgeF = false;
        boolean isForgeN = false;
        URL url;

        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            ConsoleMessages.Warn.Plugin.MALFORMED_URL.log(e, file);
        }

        try (ZipFile zip = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entryIn = entries.nextElement();
                String name = entryIn.getName();

                if (name.equals("plugin.yml")) { // NON-NLS
                    isBukkit = true;
                }

                if (name.equals("Canary.inf")) { // NON-NLS
                    isCanary = true;
                }

                if (name.endsWith(".class") && !entryIn.isDirectory()) { // NON-NLS
                    // Analyze class file
                    ClassReader classReader = new ClassReader(zip.getInputStream(entryIn));
                    GlowVisitor visitor = new GlowVisitor();
                    classReader.accept(visitor, ClassReader.SKIP_CODE
                            | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

                    if (visitor.isSponge) {
                        isSponge = true;
                    }

                    if (visitor.isForgeF) {
                        isForgeF = true;
                    }

                    if (visitor.isForgeN) {
                        isForgeN = true;
                    }
                }
            }
        } catch (IOException ex) {
            ConsoleMessages.Warn.Plugin.LOAD_FAILED.log(ex, file);
        }

        if (isBukkit) {
            bukkitPlugins.add(file);
        }
        if (isSponge) {
            spongePlugins.add(file);
        }
        if (isCanary) {
            canaryPlugins.add(file);
        }
        if (isForgeF) {
            forgefPlugins.add(file);
        }
        if (isForgeN) {
            forgenPlugins.add(file);
        }

        if (!isBukkit && !isSponge && !isCanary && !isForgeF && !isForgeN) {
            unrecognizedPlugins.add(file);
        }
    }

    private static class GlowVisitor extends ClassVisitor {

        public boolean isSponge;
        public boolean isForgeF;
        public boolean isForgeN;

        public GlowVisitor() {
            super(Opcodes.ASM5);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, boolean visible) {
            switch (name) {
                case "Lorg/spongepowered/api/plugin/Plugin;": // NON-NLS
                    isSponge = true;
                    break;
                case "Lcpw/mods/fml/common/Mod;":  // NON-NLS - older versions
                    isForgeF = true;
                    break;
                case "Lnet/minecraftforge/fml/common/Mod;":  // NON-NLS - newer
                    isForgeN = true;
                    break;
                default:
                    // do nothing
            }

            return null;
        }
    }
}
