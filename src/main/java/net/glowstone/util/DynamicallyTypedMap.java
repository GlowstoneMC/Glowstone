package net.glowstone.util;

/**
 * A map whose values are of variable types known to those invoking the getters.
 *
 * @param <K> the key type
 */
// TODO: Extend Map<K, Object>?
public interface DynamicallyTypedMap<K> {
    /**
     * Retrieves an entry as a {@link String}.
     *
     * @param key the key to look up
     * @return the value as a String
     */
    String getString(K key);

    /**
     * Retrieves an entry as an {@code int}.
     *
     * @param key the key to look up
     * @return the value as an int
     */
    int getInt(K key);

    /**
     * Retrieves an entry as a {@code boolean}.
     *
     * @param key the key to look up
     * @return the value as a boolean
     */
    boolean getBoolean(K key);
}
