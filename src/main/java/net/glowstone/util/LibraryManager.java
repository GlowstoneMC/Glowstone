package net.glowstone.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;
import org.xeustechnologies.jcl.JarClassLoader;

/**
 * Simple library manager which downloads external dependencies.
 */
public final class LibraryManager {

    /**
     * The logger for this class.
     */
    public static final Logger logger = Logger.getLogger("LibraryManager");

    /**
     * The Maven repository to download from.
     */
    final String repository;

    /**
     * The directory to store downloads in.
     */
    final File directory;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final CompletionService<Library> downloaderService = new ExecutorCompletionService<>(executorService);

    /**
     * Creates a new instance of the library manager.
     */
    public LibraryManager() {
        // todo: allow configuration of repository, libraries, and directory
        repository = "https://repo.glowstone.net/service/local/repositories/central/content/";
        directory = new File("lib");
    }

    /**
     * Downloads all runtime dependencies and adds them to a new JarClassLoader.
     */
    public JarClassLoader run() {
        if (!directory.isDirectory() && !directory.mkdirs()) {
            logger.log(Level.SEVERE, "Could not create libraries directory: " + directory);
        }

        Set<Future<Library>> futures = new HashSet<>();

        futures.add(downloaderService.submit(new LibraryDownloader("org.xerial", "sqlite-jdbc", "3.21.0", "")));
        futures.add(downloaderService.submit(new LibraryDownloader("mysql", "mysql-connector-java", "5.1.44", "")));
        futures.add(downloaderService.submit(new LibraryDownloader("org.apache.logging.log4j", "log4j-api", "2.8.1", "")));
        futures.add(downloaderService.submit(new LibraryDownloader("org.apache.logging.log4j", "log4j-core", "2.8.1", "")));

        JarClassLoader libraryClassLoader = new JarClassLoader(LibraryManager.class.getClassLoader());
        List<Library> failedLibraries = new ArrayList<>();

        while (!futures.isEmpty()) {
            Library library;
            try {
                Future<Library> libraryFuture = downloaderService.take();
                futures.remove(libraryFuture);
                library = libraryFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.log(Level.SEVERE, "Library Manager thread received an exception while waiting for futures to finish: ", e);
                for (Future<Library> future : futures) {
                    future.cancel(true);
                }
                return null;
            }

            if (library.isSuccessfullyDownloaded()) {
                try {
                    libraryClassLoader.add(library.getUri().toURL());
                } catch (MalformedURLException e) {
                    logger.log(Level.SEVERE, "Failed to get URL for library: " + library.getName() + " " + library.getVersion(), e);
                    failedLibraries.add(library);
                }
            } else {
                failedLibraries.add(library);
            }
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Library Manager thread interrupted: ", e);
            return null;
        }

        if (!failedLibraries.isEmpty()) {
            logger.log(Level.SEVERE, "Library Manager failed to download: " + failedLibraries.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\", \"", "[\"", "\"]")));
            return null;
        }

        return libraryClassLoader;
    }

    private class LibraryDownloader implements Callable<Library> {

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
        public Library call() {
            // check if we already have it
            File file = new File(directory, getLibrary());
            if (!checksum(file, checksum)) {
                // download it
                logger.info("Downloading " + library + ' ' + version + "...");
                try {
                    URL downloadUrl = new URL(repository + group.replace('.', '/') + '/' + library + '/' + version + '/' + library + '-' + version + ".jar");
                    HttpsURLConnection connection = (HttpsURLConnection) downloadUrl.openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                    try (ReadableByteChannel input = Channels.newChannel(connection.getInputStream()); FileOutputStream output = new FileOutputStream(file)) {
                        output.getChannel().transferFrom(input, 0, Long.MAX_VALUE);
                        logger.info("Downloaded " + library + ' ' + version + '.');
                    }
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Failed to download: " + library + ' ' + version, e);
                    file.delete();
                    return new Library(false, library, version, file.toURI());
                }
            }

            return new Library(true, library, version, file.toURI());
        }

        String getLibrary() {
            return library + '-' + version + ".jar";
        }

        boolean checksum(File file, String checksum) {
            // TODO: actually check checksum
            return file.exists();
        }
    }

    private static class Library {
        private final boolean successfullyDownloaded;
        private final String name;
        private final String version;
        private final URI uri;

        private Library(boolean successfullyDownloaded, String name, String version, URI uri) {
            this.successfullyDownloaded = successfullyDownloaded;
            this.name = name;
            this.version = version;
            this.uri = uri;
        }

        public boolean isSuccessfullyDownloaded() {
            return successfullyDownloaded;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public URI getUri() {
            return uri;
        }

        @Override
        public String toString() {
            return name + " " + version;
        }
    }
}
