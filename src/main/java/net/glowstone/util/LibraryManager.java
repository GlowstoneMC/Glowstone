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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

    private final ExecutorService downloaderService = Executors.newCachedThreadPool();

    public LibraryManager(GlowServer server) {
        // todo: allow configuration of repository, libraries, and directory
        repository = "http://repo.glowstone.net/service/local/repositories/central/content/";
        directory = new File("lib");
    }

    public void run() {
        if (!directory.isDirectory() && !directory.mkdirs()) {
            GlowServer.logger.log(Level.SEVERE, "Could not create libraries directory: " + directory);
        }

        downloaderService.execute(new LibraryDownloader("org.xerial", "sqlite-jdbc", "3.7.2"));
        downloaderService.execute(new LibraryDownloader("mysql", "mysql-connector-java", "5.1.38"));
        downloaderService.execute(new LibraryDownloader("org.slf4j", "slf4j-jdk14", "1.7.15"));
        downloaderService.shutdown();
        try {
            downloaderService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            GlowServer.logger.log(Level.SEVERE, "Library Manager thread interrupted: ", e);
        }
    }

    private class LibraryDownloader implements Runnable {

        private final String group;
        private final String library;
        private final String version;

        private LibraryDownloader(String group, String library, String version) {
            this.group = group;
            this.library = library;
            this.version = version;
        }

        @Override
        public void run() {
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
                        GlowServer.logger.info("Downloaded " + library + " " + version + ".");
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
}
