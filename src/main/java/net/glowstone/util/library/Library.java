package net.glowstone.util.library;

import com.google.common.collect.ComparisonChain;
import lombok.Getter;
import net.glowstone.util.library.LibraryManager.HashAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.jetbrains.annotations.NonNls;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a library that will be injected into the classpath at runtime.
 */
public class Library implements Comparable<Library> {
    private static final String ARTIFACT_ID_KEY = "artifact-id";
    private static final String CHECKSUM_KEY = "checksum";
    private static final String CHECKSUM_TYPE_KEY = "type";
    private static final String CHECKSUM_VALUE_KEY = "value";
    private static final String EXCLUDE_DEPENDENCIES_KEY = "exclude-dependencies";
    private static final String GROUP_ID_KEY = "group-id";
    private static final String REPOSITORY_KEY = "repository";
    private static final String VERSION_KEY = "version";
    /**
     * The group ID of the library in a maven-style repo. Parts of the group ID must be separated
     * by periods.
     */
    @Getter
    private final LibraryKey libraryKey;
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
     * Excludes the dependency from any dependency checks. Use this if the library is locally
     * hosted.
     */
    @Getter
    private final boolean excludeDependencies;

    /**
     * Creates a {@link Library} instance with the specified group ID, artifact ID, and version.
     *
     * @param groupId    The group ID of the library, separated by periods.
     * @param artifactId The artifact ID of the library.
     * @param version    The version of the library.
     */
    public Library(String groupId, String artifactId, String version) {
        this(groupId, artifactId, version, null, null, null, false);
    }

    /**
     * Creates a {@link Library} instance with the specified group ID, artifact ID, version, and
     * repository.
     *
     * @param groupId    The group ID of the library, separated by periods.
     * @param artifactId The artifact ID of the library.
     * @param version    The version of the library.
     * @param repository The URL of the library's repository.
     */
    public Library(String groupId, String artifactId, String version, String repository) {
        this(groupId, artifactId, version, repository, null, null, false);
    }

    /**
     * Creates a {@link Library} instance with the specified group ID, artifact ID, version, and
     * checksum.
     *
     * @param groupId       The group ID of the library, separated by periods.
     * @param artifactId    The artifact ID of the library.
     * @param version       The version of the library.
     * @param checksumType  The type of hash the checksum is using.
     * @param checksumValue The checksum to validate the downloaded library against.
     */
    public Library(@NonNls String groupId, @NonNls String artifactId, @NonNls String version,
                   HashAlgorithm checksumType, @NonNls String checksumValue) {
        this(groupId, artifactId, version, null, checksumType, checksumValue, false);
    }

    /**
     * Creates a {@link Library} instance with the specified group ID, artifact ID, version,
     * repository, and checksum.
     *
     * @param groupId             The group ID of the library, separated by periods.
     * @param artifactId          The artifact ID of the library.
     * @param version             The version of the library.
     * @param repository          The URL of the library's repository.
     * @param checksumType        The type of hash the checksum is using.
     * @param checksumValue       The checksum to validate the downloaded library against.
     * @param excludeDependencies Specifies that dependencies may be excluded.
     */
    public Library(String groupId, String artifactId, String version,
                   String repository, HashAlgorithm checksumType, String checksumValue,
                   boolean excludeDependencies) {
        this.libraryKey = new LibraryKey(groupId, artifactId);
        this.version = version;
        this.repository = StringUtils.isBlank(repository) ? null : repository;
        this.checksumType = checksumType;
        this.checksumValue = checksumValue;
        this.excludeDependencies = excludeDependencies;
    }

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
        String version = (String) configMap.get(VERSION_KEY);
        String repository = (String) configMap.get(REPOSITORY_KEY);
        HashAlgorithm checksumType = null;
        String checksumValue = null;

        Map<?, ?> checksum = (Map<?, ?>) configMap.get(CHECKSUM_KEY);
        if (checksum != null) {
            checksumType = HashAlgorithm.getAlgorithm((String) checksum.get(CHECKSUM_TYPE_KEY));
            checksumValue = (String) checksum.get(CHECKSUM_VALUE_KEY);
        }

        Boolean excludeDependencies = (Boolean) configMap.get(EXCLUDE_DEPENDENCIES_KEY);
        if (excludeDependencies == null) {
            excludeDependencies = Boolean.FALSE;
        }

        return new Library(group, artifact, version, repository, checksumType,
            checksumValue, excludeDependencies);
    }

    /**
     * Converts the {@link Library} instance to a map that can be serialized and saved into a
     * config file.
     *
     * @return A map that is able to be serialized into a config.
     */
    public Map<?, ?> toConfigMap() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put(GROUP_ID_KEY, libraryKey.getGroupId());
        configMap.put(ARTIFACT_ID_KEY, libraryKey.getArtifactId());
        configMap.put(VERSION_KEY, version);

        if (repository != null) {
            configMap.put(REPOSITORY_KEY, repository);
        }

        if (checksumType != null && checksumValue != null) {
            Map<String, Object> checksumMap = new HashMap<>();
            checksumMap.put(CHECKSUM_TYPE_KEY, checksumType.getName());
            checksumMap.put(CHECKSUM_VALUE_KEY, checksumValue);
            configMap.put(CHECKSUM_KEY, checksumMap);
        }

        if (excludeDependencies) {
            configMap.put(EXCLUDE_DEPENDENCIES_KEY, excludeDependencies);
        }

        return configMap;
    }

    // The fields the following getters represent were moved from this class to the LibraryKey
    // class. These methods are being preserved here for compatibiltiy with old code as well as
    // ease of use.

    /**
     * Returns the group ID of this library.
     */
    public String getGroupId() {
        return libraryKey.getGroupId();
    }

    /**
     * Returns the artifact ID of this library.
     */
    public String getArtifactId() {
        return libraryKey.getArtifactId();
    }

    @Override
    public String toString() {
        return libraryKey.toString() + ":" + version;
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
        return Objects.equals(libraryKey, library.libraryKey)
            && Objects.equals(version, library.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(libraryKey, version);
    }

    @Override
    public int compareTo(Library o) {
        return ComparisonChain.start()
            .compare(libraryKey, o.libraryKey)
            .compare(new ComparableVersion(version), new ComparableVersion(o.version))
            .result();
    }
}
