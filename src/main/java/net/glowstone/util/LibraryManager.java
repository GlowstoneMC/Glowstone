package net.glowstone.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;
import java.util.logging.Level;
import javax.net.ssl.HttpsURLConnection;
import net.glowstone.GlowServer;

/**
 * Simple library manager which downloads external dependencies.
 */
public final class LibraryManager {
    /**
     * The default hash function used to compute the digests.
     */
    private static final HashFunction DEFAULT_HASH_FUNCTION = Hashing.sha1();
    /**
     * The name of the hash function.
     * <p>This value is used to request the file digest from the repository.
     */
    private static final String DEFAULT_HASH_FUNCTION_NAME = "sha1";

    /**
     * The Maven repository to download from.
     */
    final String repository;

    /**
     * The directory to store downloads in.
     */
    final File directory;

    /**
     * Whether the checksum of each library should be verified after being downloaded.
     */
    final boolean validateChecksum;

    private final ExecutorService downloaderService = Executors.newCachedThreadPool();

    /**
     * Creates the instance.
     */
    public LibraryManager(String repository, String directoryName, boolean validateChecksum) {
        checkNotNull(repository);
        checkNotNull(directoryName);
        this.repository = repository;
        this.directory = new File(directoryName);
        this.validateChecksum = validateChecksum;
    }

    /**
     * Downloads the libraries.
     */
    public void run() {
        if (!directory.isDirectory() && !directory.mkdirs()) {
            GlowServer.logger
                    .log(Level.SEVERE, "Could not create libraries directory: " + directory);
        }

        downloaderService.execute(new LibraryDownloader(
                "org.xerial", "sqlite-jdbc", "3.21.0"));
        downloaderService.execute(new LibraryDownloader(
                "mysql", "mysql-connector-java", "5.1.44"));
        downloaderService.execute(new LibraryDownloader(
                "org.apache.logging.log4j", "log4j-api", "2.8.1"));
        downloaderService.execute(new LibraryDownloader(
                "org.apache.logging.log4j", "log4j-core", "2.8.1"));
        downloaderService.shutdown();
        try {
            if (!downloaderService.awaitTermination(1, TimeUnit.MINUTES)) {
                downloaderService.shutdownNow();
            }
        } catch (InterruptedException e) {
            GlowServer.logger.log(Level.SEVERE, "Library Manager thread interrupted: ", e);
        }
    }

    private class LibraryDownloader implements Runnable {

        private final String group;
        private final String library;
        private final String version;

        LibraryDownloader(String group, String library, String version) {
            this.group = group;
            this.library = library;
            this.version = version;
        }

        @Override
        public void run() {
            // check if we already have it
            File file = new File(directory, getLibrary());
            if (!file.exists()) {
                // download it
                GlowServer.logger.info("Downloading " + library + ' ' + version + "...");
                try {
                    URL downloadUrl = new URL(
                            repository + group.replace('.', '/') + '/' + library + '/' + version
                                    + '/' + library + '-' + version + ".jar");
                    HttpsURLConnection connection = (HttpsURLConnection) downloadUrl
                            .openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                    try (ReadableByteChannel input = Channels
                            .newChannel(connection.getInputStream());
                            FileOutputStream output = new FileOutputStream(file)) {
                        output.getChannel().transferFrom(input, 0, Long.MAX_VALUE);
                        GlowServer.logger.info("Downloaded " + library + ' ' + version + '.');
                    }

                    if (validateChecksum) {
                        // download checksum
                        URL checksumUrl = new URL(downloadUrl.toString() + "." + DEFAULT_HASH_FUNCTION_NAME);
                        connection = (HttpsURLConnection) checksumUrl
                                .openConnection();
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String checksum = reader.readLine();
                        reader.close();
                        if (!checksum(file, checksum, DEFAULT_HASH_FUNCTION)) {
                            GlowServer.logger.log(Level.WARNING, "Checksum verification failed for " + file.getName() + ".");
                            file.delete();
                        } else {
                            GlowServer.logger.info("Checksum validated for " + file.getName());
                        }
                    }
                } catch (IOException e) {
                    GlowServer.logger.log(Level.WARNING,
                            "Failed to download: " + library + ' ' + version, e);
                    file.delete();
                    return;
                }
            }

            // hack it onto the classpath
            try {
                String[] javaVersion = System.getProperty("java.version").split("-")[0]
                        .split("\\.");
                if (Integer.parseInt(javaVersion[0]) >= 9) {
                    ClassPathAgent.addJarFile(new JarFile(file));
                } else {
                    Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    method.setAccessible(true);
                    method.invoke(ClassLoader.getSystemClassLoader(), file.toURI().toURL());
                }
            } catch (ReflectiveOperationException | IOException e) {
                GlowServer.logger.log(Level.WARNING,
                        "Failed to add to classpath: " + library + " " + version, e);
            }
        }

        String getLibrary() {
            return library + '-' + version + ".jar";
        }

        /**
         * Computes and validates the checksum of a file.
         *
         * @param file the file.
         * @param checksum the reference checksum to validate the file's digest with.
         * @param hashFunction the {@link HashFunction} used to compute the file's digest.
         * @return true if the checksum was validated, false otherwise.
         */
        boolean checksum(File file, String checksum, HashFunction hashFunction) {
            checkNotNull(file);
            checkNotNull(hashFunction);
            if (!file.exists()) {
                return false;
            }
            if (checksum == null || checksum.isEmpty()) {
                // assume everything is OK if no reference checksum is provided
                return true;
            }
            // get the file digest
            String digest;
            try {
                digest = Files.hash(file, hashFunction).toString();
            } catch (IOException ex) {
                GlowServer.logger.log(Level.SEVERE, "Failed to compute digest for '" + file.getName() + "'", ex);
                return false;
            }
            return digest.equals(checksum);
        }
    }
}
