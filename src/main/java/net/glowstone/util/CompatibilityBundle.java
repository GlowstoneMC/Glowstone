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
            new Library("org.xerial", "sqlite-jdbc", "3.21.0.1",
                LibraryManager.HashAlgorithm.SHA1, "81a0bcda2f100dc91dc402554f60ed2f696cded5"),
            new Library("mysql", "mysql-connector-java", "5.1.46",
                LibraryManager.HashAlgorithm.SHA1, "9a3e63b387e376364211e96827bc27db8d7a92e9"),
            new Library("org.apache.logging.log4j", "log4j-slf4j-impl", "2.17.1",
                    LibraryManager.HashAlgorithm.SHA1, "84692d456bcce689355d33d68167875e486954dd"),
            new Library("org.apache.logging.log4j", "log4j-core", "2.17.1",
                LibraryManager.HashAlgorithm.SHA1, "779f60f3844dadc3ef597976fcb1e5127b1f343d"),
            new Library("org.apache.logging.log4j", "log4j-iostreams", "2.17.1",
                LibraryManager.HashAlgorithm.SHA1, "6ebd6d2186fae85d294b5d1994d711242d314427"),
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
