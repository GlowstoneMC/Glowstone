package net.glowstone.util;

import net.glowstone.GlowServer;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
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
    final String repository;

    /**
     * The directory to store downloads in.
     */
    final File directory;

    private final ExecutorService downloaderService = Executors.newCachedThreadPool();

    private static final LibraryClassLoader loader = new LibraryClassLoader();

    public LibraryManager() {
        // todo: allow configuration of repository, libraries, and directory
        repository = "https://repo.glowstone.net/service/local/repositories/central/content/";
        directory = new File("lib");
    }

    public static void addToClasspath(String... paths) {
        try {
            for (String path : Objects.requireNonNull(paths)) {
                String trimmedPath;
                if (path != null && !(trimmedPath = path.trim()).isEmpty()) {
                    loader.addURL(Paths.get(trimmedPath).toUri().toURL());
                }
            }
        } catch (IllegalArgumentException | MalformedURLException e) {
            RuntimeException re = new RuntimeException(e);
            re.setStackTrace(e.getStackTrace());
            throw re;
        }
    }

    public static ClassLoader getLibraryClassLoader() {
        return loader;
    }

    public static void addToLibraryPath(String... paths) {
        for (String path : Objects.requireNonNull(paths)) {
            loader.addLibPath(path);
        }
    }

    public void run() {
        if (!directory.isDirectory() && !directory.mkdirs()) {
            GlowServer.logger.log(Level.SEVERE, "Could not create libraries directory: " + directory);
        }

        downloaderService.execute(new LibraryDownloader("org.xerial", "sqlite-jdbc", "3.16.1", ""));
        downloaderService.execute(new LibraryDownloader("mysql", "mysql-connector-java", "5.1.42", ""));
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
        private String checksum;

        LibraryDownloader(String group, String library, String version, String checksum) {
            this.group = group;
            this.library = library;
            this.version = version;
            this.checksum = checksum;
        }

        @Override
        public void run() {
            // check if we already have it
            File file = new File(directory, library + '-' + version + ".jar");
            if (!file.exists() && checksum(file, checksum)) {
                // download it
                GlowServer.logger.info("Downloading " + library + ' ' + version + "...");
                try {
                    URL downloadUrl = new URL(repository + group.replace('.', '/') + '/' + library + '/' + version + '/' + library + '-' + version + ".jar");
                    HttpsURLConnection connection = (HttpsURLConnection) downloadUrl.openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                    try (ReadableByteChannel input = Channels.newChannel(connection.getInputStream());
                         FileOutputStream output = new FileOutputStream(file)) {
                        output.getChannel().transferFrom(input, 0, Long.MAX_VALUE);
                        GlowServer.logger.info("Downloaded " + library + ' ' + version + '.');
                    }
                } catch (IOException e) {
                    GlowServer.logger.log(Level.WARNING, "Failed to download: " + library + ' ' + version, e);
                    return;
                }
            }

            addToClasspath(file.getAbsolutePath());
        }

        public boolean checksum(File file, String checksum) {
            // TODO: actually check checksum
            return true;
        }
    }

    private static class LibraryClassLoader extends URLClassLoader {

        static {
            ClassLoader.registerAsParallelCapable();
        }

        private final Set<Path> libPaths = new CopyOnWriteArraySet<>();

        private LibraryClassLoader() {
            super(new URL[0]);
        }

        @Override
        protected void addURL(URL url) {
            super.addURL(url);
        }

        protected void addLibPath(String path) {
            libPaths.add(Paths.get(path).toAbsolutePath());
        }

        @Override
        protected String findLibrary(String libname) {
            String nativeName = System.mapLibraryName(libname);
            return libPaths.stream().map(path -> path.resolve(nativeName)).filter(Files::exists).map(Path::toString).findFirst().orElse(super.findLibrary(libname));
        }
    }
}
