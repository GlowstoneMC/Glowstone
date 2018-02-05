package net.glowstone.util;

public interface DynamicallyTypedMapWithFloats<K> extends DynamicallyTypedMap<K> {
    /**
     * Retrieves an entry as a {@code float}.
     *
     * @param key the key to look up
     * @return the value as a {@code float}
     */
    float getFloat(K key);

}
