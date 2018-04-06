package net.glowstone;

import static net.glowstone.LocalizedStrings.logInfo;

import com.google.common.io.PatternFilenameFilter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

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
        LocalizedStrings.logInfo("console.info.plugin.scanning");
        File[] files = directory.listFiles(new PatternFilenameFilter(".+\\.jar"));
        if (files == null || files.length == 0) {
            return;
        }

        for (File file : files) {
            scanFile(file);
        }

        logInfo("console.info.plugin.counts", bukkitPlugins.size(),
                spongePlugins.size(), forgefPlugins.size() + forgenPlugins.size(),
                canaryPlugins.size(), unrecognizedPlugins.size(), files.length);

        if (!unrecognizedPlugins.isEmpty()) {
            for (File file : unrecognizedPlugins) {
                GlowServer.logger.warning("Unrecognized plugin: " + file.getPath());
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
            GlowServer.logger.log(Level.WARNING, "PluginTypeDetector: Malformed URL: " + file, e);
            return;
        }

        try (ZipFile zip = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entryIn = entries.nextElement();
                String name = entryIn.getName();

                if (name.equals("plugin.yml")) {
                    isBukkit = true;
                }

                if (name.equals("Canary.inf")) {
                    isCanary = true;
                }

                if (name.endsWith(".class") && !entryIn.isDirectory()) {
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
            GlowServer.logger.log(Level.WARNING, "PluginTypeDetector: Error reading " + url, ex);
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
                case "Lorg/spongepowered/api/plugin/Plugin;":
                    isSponge = true;
                    break;
                case "Lcpw/mods/fml/common/Mod;":  // older versions
                    isForgeF = true;
                    break;
                case "Lnet/minecraftforge/fml/common/Mod;":  // newer
                    isForgeN = true;
                    break;
                default:
                    // do nothing
            }

            return null;
        }
    }
}
