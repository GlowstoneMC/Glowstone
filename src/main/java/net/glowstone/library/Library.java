package net.glowstone.library;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.glowstone.library.LibraryManager.HashAlgorithm;

public class Library {
    private static final String ARTIFACT_ID_KEY = "artifact-id";
    private static final String CHECKSUM_KEY = "checksum";
    private static final String CHECKSUM_TYPE_KEY = "type";
    private static final String CHECKSUM_VALUE_KEY = "value";
    private static final String GROUP_ID_KEY = "group-id";
    private static final String REPOSITORY_KEY = "repository";
    private static final String VERSION_KEY = "version";

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

    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String repository;
    private final HashAlgorithm checksumType;
    private final String checksumValue;

    public Library(String groupId, String artifactId, String version) {
        this(groupId, artifactId, version, null, null, null);
    }

    public Library(String groupId, String artifactId, String version, String repository) {
        this(groupId, artifactId, version, repository, null, null);
    }

    public Library(String groupId, String artifactId, String version, HashAlgorithm checksumType,
                   String checksumValue) {
        this(groupId, artifactId, version, null, checksumType, checksumValue);
    }

    public Library(String groupId, String artifactId, String version,
                   String repository, HashAlgorithm checksumType, String checksumValue) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.repository = repository;
        this.checksumType = checksumType;
        this.checksumValue = checksumValue;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getRepository() {
        return repository;
    }

    public HashAlgorithm getChecksumType() {
        return checksumType;
    }

    public String getChecksumValue() {
        return checksumValue;
    }

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
        return Objects.equals(groupId, library.groupId) &&
                Objects.equals(artifactId, library.artifactId) &&
                Objects.equals(version, library.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
    }
}
