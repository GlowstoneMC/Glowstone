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
     * The default hash function used to compute the digests.
     */
    private static final HashFunction DEFAULT_HASH_FUNCTION = Hashing.sha1();
    /**
     * The alternative hash function used to compute the digests.
     */
    private static final HashFunction ALTERNATIVE_HASH_FUNCTION = Hashing.md5();
    /**
     * The default {@link HashAlgorithm} used by Glowstone for server libraries.
     */
    private static final HashAlgorithm DEFAULT_HASH_ALGORITHM = HashAlgorithm.SHA1;
    /**
     * The name of the default hash function.
     */
    private static final String DEFAULT_HASH_FUNCTION_NAME = "sha1";
    /**
     * The name of the alternative hash function.
     */
    private static final String ALTERNATIVE_HASH_FUNCTION_NAME = "md5";

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
                "org.xerial", "sqlite-jdbc", "3.21.0",
                "347e4d1d3e1dff66d389354af8f0021e62344584", DEFAULT_HASH_ALGORITHM));
        downloaderService.execute(new LibraryDownloader(
                "mysql", "mysql-connector-java", "5.1.44",
                "61b6b998192c85bb581c6be90e03dcd4b9079db4", DEFAULT_HASH_ALGORITHM));
        downloaderService.execute(new LibraryDownloader(
                "org.apache.logging.log4j", "log4j-api", "2.8.1",
                "e801d13612e22cad62a3f4f3fe7fdbe6334a8e72", DEFAULT_HASH_ALGORITHM));
        downloaderService.execute(new LibraryDownloader(
                "org.apache.logging.log4j", "log4j-core", "2.8.1",
                "4ac28ff2f1ddf05dae3043a190451e8c46b73c31", DEFAULT_HASH_ALGORITHM));
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

                    if (validateChecksum && algorithm != null && checksum != null
                            && !checksum(file, checksum, algorithm)) {
                        GlowServer.logger.severe("The checksum for the library '" + getLibrary()
                                + "' does not match. "
                                + "Restart the server to download it again.");
                        file.delete();
                        return;
                    }
                } catch (IOException e) {
                    GlowServer.logger.log(Level.WARNING,
                            "Failed to download: " + library + ' ' + version, e);
                    file.delete();
                    return;
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
         * <p>If the file does not exist, the checksum will be automatically invalidated.
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

    public enum HashAlgorithm {

        SHA1(DEFAULT_HASH_FUNCTION, DEFAULT_HASH_FUNCTION_NAME),
        MD5(ALTERNATIVE_HASH_FUNCTION, ALTERNATIVE_HASH_FUNCTION_NAME);

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
            // add the algorithm to the map
            for (HashAlgorithm algorithm : values()) {
                BY_NAME.put(algorithm.getName(), algorithm);
            }
        }
    }
}
