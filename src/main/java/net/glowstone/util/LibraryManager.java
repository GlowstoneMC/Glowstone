package net.glowstone.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Maps;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;
import java.util.logging.Level;
import javax.net.ssl.HttpsURLConnection;
import lombok.Getter;
import net.glowstone.GlowServer;
import net.glowstone.util.config.ServerConfig;

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

    /**
     * Whether the checksum of each library should be verified after being downloaded.
     */
    final boolean validateChecksum;

    /**
     * The maximum amount of attempts to download each library.
     */
    final int maxDownloadAttempts;

    private final ExecutorService downloaderService = Executors.newCachedThreadPool();

    /**
     * Creates the instance.
     *
     * @param repository the repository to download the libraries from
     * @param directoryName the name of the directory to download the libraries to
     * @param validateChecksum whether or not checksum validation is enabled
     */
    public LibraryManager(String repository, String directoryName, boolean validateChecksum, int maxDownloadAttempts) {
        checkNotNull(repository);
        checkNotNull(directoryName);
        this.repository = repository;
        this.directory = new File(directoryName);
        this.validateChecksum = validateChecksum;
        this.maxDownloadAttempts = maxDownloadAttempts;
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
                "org.xerial", "sqlite-jdbc", "3.21.0",
                "347e4d1d3e1dff66d389354af8f0021e62344584", HashAlgorithm.SHA1));
        downloaderService.execute(new LibraryDownloader(
                "mysql", "mysql-connector-java", "5.1.44",
                "61b6b998192c85bb581c6be90e03dcd4b9079db4", HashAlgorithm.SHA1));
        downloaderService.execute(new LibraryDownloader(
                "org.apache.logging.log4j", "log4j-api", "2.8.1",
                "e801d13612e22cad62a3f4f3fe7fdbe6334a8e72", HashAlgorithm.SHA1));
        downloaderService.execute(new LibraryDownloader(
                "org.apache.logging.log4j", "log4j-core", "2.8.1",
                "4ac28ff2f1ddf05dae3043a190451e8c46b73c31", HashAlgorithm.SHA1));
        downloaderService.execute(new LibraryDownloader(
                "org.apache.commons", "commons-lang3", "3.5",
                "6c6c702c89bfff3cd9e80b04d668c5e190d588c6", HashAlgorithm.SHA1));
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
        private final String checksum;
        private final HashAlgorithm algorithm;

        /**
         * Creates an instance of the downloader for a library.
         *
         * @param group the Maven group ID of the library
         * @param library the Maven artifact ID of the library
         * @param version the Maven artifact version of the library
         * @param checksum the reference checksum of the library.
         *                  See {@link LibraryDownloader#checksum(File, String, HashAlgorithm)}
         * @param algorithm the algorithm used to validate the checksum.
         *                   See {@link LibraryDownloader#checksum(File, String, HashAlgorithm)}
         */
        LibraryDownloader(String group, String library, String version, String checksum, HashAlgorithm algorithm) {
            this.group = group;
            this.library = library;
            this.version = version;
            this.checksum = checksum;
            this.algorithm = algorithm;
        }

        @Override
        public void run() {
            // check if we already have it
            File file = new File(directory, getLibrary());
            if (!file.exists()) {
                int attempts = 0;
                while (attempts < maxDownloadAttempts) {
                    attempts++;
                    // download it
                    GlowServer.logger.info("Downloading " + library + ' ' + version + "...");
                    try {
                        URL downloadUrl = new URL(
                                repository + group.replace('.', '/') + '/'
                                        + library + '/' + version + '/' + library + '-' + version + ".jar");
                        HttpsURLConnection connection = (HttpsURLConnection) downloadUrl
                                .openConnection();
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                        try (ReadableByteChannel input = Channels
                                .newChannel(connection.getInputStream());
                             FileOutputStream output = new FileOutputStream(file)) {
                            output.getChannel().transferFrom(input, 0, Long.MAX_VALUE);
                            GlowServer.logger.info("Downloaded " + library + ' ' + version + '.');
                        }

                        Hashing.crc32c();

                        if (validateChecksum && algorithm != null && checksum != null
                                && !checksum(file, checksum, algorithm)) {
                            GlowServer.logger.severe("The checksum for the library '" + getLibrary()
                                    + "' does not match. "
                                    + (attempts == maxDownloadAttempts ? "Restart the server to attempt "
                                    + "downloading it again."
                                            : "Attempting download again ("
                                                    + (attempts + 1) + "/" + maxDownloadAttempts + ")"));
                            file.delete();
                            if (attempts == maxDownloadAttempts) {
                                return;
                            }
                            continue;
                        }
                        // everything's fine
                        break;
                    } catch (IOException e) {
                        GlowServer.logger.log(Level.WARNING,
                                "Failed to download: " + library + ' ' + version, e);
                        file.delete();
                        if (attempts == maxDownloadAttempts) {
                            GlowServer.logger.warning("Restart the server to attempt downloading '"
                                    + getLibrary() + "' again.");
                            return;
                        }
                        GlowServer.logger.warning("Attempting download of '" + getLibrary() + "' again ("
                                + (attempts + 1) + "/" + maxDownloadAttempts + ")");
                    }
                }
            } else if (validateChecksum && algorithm != null && checksum != null
                    && !checksum(file, checksum, algorithm)) {
                // The file is already downloaded, but validate the checksum as a warning only
                GlowServer.logger.warning("The checksum for the library '" + getLibrary()
                        + "' does not match. "
                        + "Remove the library and restart the server to download it again.");
                GlowServer.logger.warning("Additionally, you can disable this warning in the server "
                        + "configuration, under '" + ServerConfig.Key.LIBRARY_CHECKSUM_VALIDATION.getPath() + "'.");
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
            } catch (Exception e) {
                GlowServer.logger.log(Level.WARNING,
                        "Failed to add to classpath: " + library + " " + version, e);
            }
        }

        /**
         * Gets the name of the file the library will be saved to.
         *
         * @return the name of the file the library will be saved to
         */
        String getLibrary() {
            return library + '-' + version + ".jar";
        }

        /**
         * Computes and validates the checksum of a file.
         *
         * <p>If the file does not exist, the checksum will be automatically invalidated.
         *
         * <p>If the reference checksum or the algorithm are empty or null,
         * the checksum will be automatically validated.
         *
         * @param file the file.
         * @param checksum the reference checksum to validate the file's digest with.
         * @param algorithm the {@link HashAlgorithm} used to compute the file's digest.
         * @return true if the checksum was validated, false otherwise.
         */
        boolean checksum(File file, String checksum, HashAlgorithm algorithm) {
            checkNotNull(file);
            if (!file.exists()) {
                return false;
            }
            if (algorithm == null || checksum == null || checksum.isEmpty()) {
                // assume everything is OK if no reference checksum is provided
                return true;
            }
            // get the file digest
            String digest;
            try {
                digest = Files.hash(file, algorithm.getFunction()).toString();
            } catch (IOException ex) {
                GlowServer.logger.log(Level.SEVERE, "Failed to compute digest for '" + file.getName() + "'", ex);
                return false;
            }
            return digest.equals(checksum);
        }
    }

    /**
     * An enum containing the supported hash algorithms.
     */
    public enum HashAlgorithm {
        /**
         * The SHA-1 hash algorithm.
         */
        SHA1(Hashing.sha1(), "sha1"),
        /**
         * The MD5 hash algorithm.
         */
        MD5(Hashing.md5(), "md5");

        /**
         * The hash function for this algorithm.
         */
        @Getter
        private final HashFunction function;
        /**
         * The name of the algorithm, used in configuration files.
         */
        @Getter
        private final String name;

        private static final Map<String, HashAlgorithm> BY_NAME = Maps.newHashMap();

        /**
         * Represents a hash algorithm.
         *
         * @param function the {@link HashFunction} used to calculate the hash
         * @param name the name of the algorithm
         */
        HashAlgorithm(HashFunction function, String name) {
            checkNotNull(function);
            checkNotNull(name);

            this.function = function;
            this.name = name;
        }

        /**
         * Gets the hash algorithm corresponding to the given name.
         *
         * @param name the name of the algorithm
         * @return the corresponding algorithm, or null if none exists
         */
        public static HashAlgorithm getAlgorithm(String name) {
            checkNotNull(name);

            return BY_NAME.get(name.toLowerCase());
        }

        static {
            // add the algorithms to the map
            for (HashAlgorithm algorithm : values()) {
                BY_NAME.put(algorithm.getName(), algorithm);
            }
        }
    }
}
