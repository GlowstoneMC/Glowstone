package net.glowstone.util.library;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import net.glowstone.util.library.LibraryManager.HashAlgorithm;

/**
 * Represents a library that will be injected into the classpath at runtime.
 */
public class Library {
    private static final String ARTIFACT_ID_KEY = "artifact-id";
    private static final String CHECKSUM_KEY = "checksum";
    private static final String CHECKSUM_TYPE_KEY = "type";
    private static final String CHECKSUM_VALUE_KEY = "value";
    private static final String GROUP_ID_KEY = "group-id";
    private static final String REPOSITORY_KEY = "repository";
    private static final String VERSION_KEY = "version";

    /**
     * Extracts the needed information from a map of key-value pairs inside a config file to
     * download a {@link Library} and inject it at runtime.
     *
     * @param configMap The Map that was extracted from a config file.
     * @return A {@link Library} instance populated with whatever information we could extract.
     */
    @SuppressWarnings("unchecked")
    public static Library fromConfigMap(Map<?, ?> configMap) {
        String group = (String) configMap.get(GROUP_ID_KEY);
        String artifact = (String) configMap.get(ARTIFACT_ID_KEY);
        String version  = (String) configMap.get(VERSION_KEY);
        String repository = (String) configMap.get(REPOSITORY_KEY);
        HashAlgorithm checksumType = null;
        String checksumValue = null;

        Map<?, ?> checksum = (Map<?, ?>) configMap.get(CHECKSUM_KEY);
        if (checksum != null) {
            checksumType = HashAlgorithm.getAlgorithm((String) checksum.get(CHECKSUM_TYPE_KEY));
            checksumValue = (String) checksum.get(CHECKSUM_VALUE_KEY);
        }

        return new Library(group, artifact, version, repository, checksumType,
                checksumValue);
    }

    /**
     * The group ID of the library in a maven-style repo. Parts of the group ID must be separated
     * by periods.
     */
    @Getter
    private final String groupId;

    /**
     * The artifact ID of the library in a maven-style repo.
     */
    @Getter
    private final String artifactId;

    /**
     * The version number of the library in a maven-style repo.
     */
    @Getter
    private final String version;

    /**
     * The optional URL for this library, for use in cases where the library is not part of the
     * default Maven repo.
     */
    @Getter
    private final String repository;

    /**
     * The algorithm used to generate the checksum for this library, if one was specified.
     */
    @Getter
    private final HashAlgorithm checksumType;

    /**
     * The checksum itself, validated against the library to make sure the library is intact.
     */
    @Getter
    private final String checksumValue;

    /**
     * Creates a {@link Library} instance with the specified group ID, artifact ID, and version.
     *
     * @param groupId The group ID of the library, separated by periods.
     * @param artifactId The artifact ID of the library.
     * @param version The version of the library.
     */
    public Library(String groupId, String artifactId, String version) {
        this(groupId, artifactId, version, null, null, null);
    }

    /**
     * Creates a {@link Library} instance with the specified group ID, artifact ID, version, and
     * repository.
     *
     * @param groupId The group ID of the library, separated by periods.
     * @param artifactId The artifact ID of the library.
     * @param version The version of the library.
     * @param repository The URL of the library's repository.
     */
    public Library(String groupId, String artifactId, String version, String repository) {
        this(groupId, artifactId, version, repository, null, null);
    }

    /**
     * Creates a {@link Library} instance with the specified group ID, artifact ID, version, and
     * checksum.
     *
     * @param groupId The group ID of the library, separated by periods.
     * @param artifactId The artifact ID of the library.
     * @param version The version of the library.
     * @param checksumType The type of hash the checksum is using.
     * @param checksumValue The checksum to validate the downloaded library against.
     */
    public Library(String groupId, String artifactId, String version, HashAlgorithm checksumType,
                   String checksumValue) {
        this(groupId, artifactId, version, null, checksumType, checksumValue);
    }

    /**
     * Creates a {@link Library} instance with the specified group ID, artifact ID, version,
     * repository, and checksum.
     *
     * @param groupId The group ID of the library, separated by periods.
     * @param artifactId The artifact ID of the library.
     * @param version The version of the library.
     * @param repository The URL of the library's repository.
     * @param checksumType The type of hash the checksum is using.
     * @param checksumValue The checksum to validate the downloaded library against.
     */
    public Library(String groupId, String artifactId, String version,
                   String repository, HashAlgorithm checksumType, String checksumValue) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.repository = repository;
        this.checksumType = checksumType;
        this.checksumValue = checksumValue;
    }

    /**
     * Converts the {@link Library} instance to a map that can be serialized and saved into a
     * config file.
     *
     * @return A map that is able to be serialized into a config.
     */
    public Map<?, ?> toConfigMap() {
        // Using LinkedHashMap to keep the props in order when written into the config file.
        Map<String, Object> configMap = new LinkedHashMap<>();
        configMap.put(GROUP_ID_KEY, groupId);
        configMap.put(ARTIFACT_ID_KEY, artifactId);
        configMap.put(VERSION_KEY, version);

        if (repository != null) {
            configMap.put(REPOSITORY_KEY, repository);
        }

        if (checksumType != null && checksumValue != null) {
            Map<String, Object> checksumMap = new LinkedHashMap<>();
            checksumMap.put(CHECKSUM_TYPE_KEY, checksumType.getName());
            checksumMap.put(CHECKSUM_VALUE_KEY, checksumValue);
            configMap.put(CHECKSUM_KEY, checksumMap);
        }

        return configMap;
    }

    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Library library = (Library) o;
        return Objects.equals(groupId, library.groupId)
                && Objects.equals(artifactId, library.artifactId)
                && Objects.equals(version, library.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
    }
}
