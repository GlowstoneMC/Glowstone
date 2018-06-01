package net.glowstone.util.library;

import com.google.common.collect.ComparisonChain;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NonNls;

/**
 * Encapsulates the identifying pieces of a library in a Maven repository, its group ID and
 * artifact ID. Can be safely used as a key in a map or within a set.
 */
@EqualsAndHashCode
public class LibraryKey implements Comparable<LibraryKey> {
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

    public LibraryKey(@NonNls String groupId, @NonNls String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    @Override
    public String toString() {
        return groupId + ":" + artifactId;
    }

    @Override
    public int compareTo(LibraryKey o) {
        return ComparisonChain.start()
                .compare(groupId, o.groupId)
                .compare(artifactId, o.artifactId)
                .result();
    }
}
