package net.glowstone.util;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;
import net.glowstone.util.library.Library;
import net.glowstone.util.library.LibraryKey;
import net.glowstone.util.library.LibraryManager;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Compatibility bundles are bundles of libraries that other servers include in their servers
 * but Glowstone does not. We will download the libraries included in the bundle specified
 * within the Glowstone config.
 */
public enum CompatibilityBundle {
    CRAFTBUKKIT(
        Stream.of(
            new Library("org.xerial", "sqlite-jdbc", "3.36.0.3",
                LibraryManager.HashAlgorithm.SHA1, "7fa71c4dfab806490cb909714fb41373ec552c29"),
            new Library("mysql", "mysql-connector-java", "8.0.29",
                LibraryManager.HashAlgorithm.SHA1, "016bfffda393ac4fe56f0985f1f035b37d3fc48f"),
            new Library("org.apache.logging.log4j", "log4j-core", "2.17.1",
                LibraryManager.HashAlgorithm.SHA1, "779f60f3844dadc3ef597976fcb1e5127b1f343d"),
            new Library("org.apache.logging.log4j", "log4j-iostreams", "2.17.1",
                LibraryManager.HashAlgorithm.SHA1, "6ebd6d2186fae85d294b5d1994d711242d314427")
        )
            .collect(ImmutableMap.toImmutableMap(Library::getLibraryKey, Function.identity()))
    ),
    NONE(ImmutableMap.of());

    public final Map<LibraryKey, Library> libraries;

    CompatibilityBundle(Map<LibraryKey, Library> libraries) {
        this.libraries = libraries;
    }

    /**
     * Converts the given config value into the appropriate bundle. If the given value is blank or
     * null, the default value is returned. If the given value does not match any preprogrammmed
     * bundles case insensitively, then null is returned.
     *
     * @param configValue The value from the config file.
     */
    public static CompatibilityBundle fromConfig(String configValue) {
        if (configValue == null || CharMatcher.whitespace().matchesAllOf(configValue)) {
            return CompatibilityBundle.CRAFTBUKKIT;
        }
        try {
            return valueOf(configValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
