package net.glowstone.util;

import net.glowstone.GlowServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;

/**
 * Simple library manager which downloads external dependencies.
 */
public final class LibraryManager {

    /**
     * The Maven repository to download from.
     */
    private final String repository;

    /**
     * The directory to store downloads in.
     */
    private final File directory;

    public LibraryManager(GlowServer server) {
        // todo: allow configuration of repository, libraries, and directory
        repository = "http://repo.glowstone.net/content/groups/public/";
        directory = new File("lib");
    }

    public void run() {
        if (!directory.isDirectory() && !directory.mkdirs()) {
            GlowServer.logger.log(Level.SEVERE, "Could not create libraries directory: " + directory);
        }

        download("org.xerial", "sqlite-jdbc", "3.7.2");
        download("mysql", "mysql-connector-java", "5.1.34"); // was 5.1.14
    }

    private void download(String group, String library, String version) {
        // check if we already have it
        File file = new File(directory, library + "-" + version + ".jar");
        if (!file.exists()) {
            // download it
            GlowServer.logger.info("Downloading " + library + " " + version + "...");
            try {
                URL downloadUrl = new URL(repository + group.replace('.', '/') + "/" + library + "/" + version + "/" + library + "-" + version + ".jar");
                try (ReadableByteChannel input = Channels.newChannel(downloadUrl.openStream());
                     FileOutputStream output = new FileOutputStream(file)) {
                    output.getChannel().transferFrom(input, 0, Long.MAX_VALUE);
                }
            } catch (IOException e) {
                GlowServer.logger.log(Level.WARNING, "Failed to download: " + library + " " + version, e);
                return;
            }
        }

        // hack it onto the classpath
        URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysLoader, file.toURI().toURL());
        } catch (ReflectiveOperationException | MalformedURLException e) {
            GlowServer.logger.log(Level.WARNING, "Failed to add to classpath: " + library + " " + version, e);
        }
    }
}
