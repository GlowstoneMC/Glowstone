package net.glowstone.util;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.glowstone.util.library.Library;
import net.glowstone.util.library.LibraryKey;
import net.glowstone.util.library.LibraryManager;

/**
 * Compatibility bundles are bundles of libraries that other servers include in their servers
 * but Glowstone does not. We will download the libraries included in the bundle specified
 * within the Glowstone config.
 */
public enum CompatibilityBundle {
    CRAFTBUKKIT(
        Stream.of(
            new Library("org.xerial", "sqlite-jdbc", "3.21.0.1",
                LibraryManager.HashAlgorithm.SHA1, "347e4d1d3e1dff66d389354af8f0021e62344584"),
            new Library("mysql", "mysql-connector-java", "5.1.46",
                LibraryManager.HashAlgorithm.SHA1, "61b6b998192c85bb581c6be90e03dcd4b9079db4"),
            new Library("org.apache.logging.log4j", "log4j-api", "2.8.2",
                LibraryManager.HashAlgorithm.SHA1, "e801d13612e22cad62a3f4f3fe7fdbe6334a8e72"),
            new Library("org.apache.logging.log4j", "log4j-core", "2.8.2",
                LibraryManager.HashAlgorithm.SHA1, "4ac28ff2f1ddf05dae3043a190451e8c46b73c31"),
            new Library("org.apache.commons", "commons-lang3", "3.5",
                LibraryManager.HashAlgorithm.SHA1, "6c6c702c89bfff3cd9e80b04d668c5e190d588c6")
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
