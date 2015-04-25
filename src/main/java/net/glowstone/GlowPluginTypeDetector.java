package net.glowstone;

import com.google.common.io.PatternFilenameFilter;
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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private Logger logger;

    public GlowPluginTypeDetector(File directory, Logger logger) {
        this.directory = directory;
        this.logger = logger;
    }

    public void scan() {
        logger.info("Scanning plugins...");
        File[] files = directory.listFiles(new PatternFilenameFilter(".+\\.jar"));
        if (files == null || files.length == 0) {
            return;
        }

        for (File file : files) {
            scanFile(file);
        }

        logger.info("PluginTypeDetector: found " +
                bukkitPlugins.size() + " Bukkit, " +
                spongePlugins.size() + " Sponge, " +
                (forgefPlugins.size() + forgenPlugins.size()) + " Forge, " +
                canaryPlugins.size() + " Canary, " +
                unrecognizedPlugins.size() + " unknown plugins (total " + files.length + ")");

        if (unrecognizedPlugins.size() != 0) {
            for (File file : unrecognizedPlugins) {
                logger.warning("Unrecognized plugin: " + file.getPath());
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
            logger.log(Level.WARNING, "PluginTypeDetector: Malformed URL: " + file, e);
            return;
        }

        try (ZipFile zip = new ZipFile(file);
        ) {
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
                    classReader.accept(visitor, 0);

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
            logger.log(Level.WARNING, "PluginTypeDetector: Error reading " + url, ex);
        }

        if (isBukkit) bukkitPlugins.add(file);
        if (isSponge) spongePlugins.add(file);
        if (isCanary) canaryPlugins.add(file);
        if (isForgeF) forgefPlugins.add(file);
        if (isForgeN) forgenPlugins.add(file);

        if (!isBukkit && !isSponge && !isCanary && !isForgeF && !isForgeN) unrecognizedPlugins.add(file);
    }

    private class GlowVisitor extends ClassVisitor {
        public boolean isSponge;
        public boolean isForgeF;
        public boolean isForgeN;

        public GlowVisitor() {
            super(Opcodes.ASM5);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, boolean visible) {
            if (name.equals("Lorg/spongepowered/api/plugin/Plugin;")) {
                isSponge = true;
            } else if (name.equals("Lcpw/mods/fml/common/Mod;")) { // older versions
                isForgeF = true;
            } else if (name.equals("Lnet/minecraftforge/fml/common/Mod;")) { // newer
                isForgeN = true;
            }

            return null;
        }
    }
}
