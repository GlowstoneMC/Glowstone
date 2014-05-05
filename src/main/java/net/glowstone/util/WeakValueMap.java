package net.glowstone.util;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A simple mapping from strongly-referenced keys to weakly-referenced values
 * based on the {@link ConcurrentHashMap} class.
 */
public class WeakValueMap<K, V> {

    private final ConcurrentMap<K, Reference<V>> map = new ConcurrentHashMap<>();

    private final boolean soft;

    public WeakValueMap() {
        this(false);
    }

    public WeakValueMap(boolean soft) {
        this.soft = soft;
    }

    private V unwrap(Reference<V> ref) {
        return ref == null ? null : ref.get();
    }

    private Reference<V> wrap(V value) {
        return soft ? new SoftReference<>(value) : new WeakReference<>(value);
    }

    public V get(K key) {
        return unwrap(map.get(key));
    }

    public V getOrCreate(K key, V value) {
        V prevValue = unwrap(map.putIfAbsent(key, wrap(value)));
        return prevValue == null ? value : prevValue;
    }

    public V put(K key, V value) {
        return unwrap(map.put(key, wrap(value)));
    }

    public V remove(K key) {
        return unwrap(map.remove(key));
    }

    public boolean containsKey(K key) {
        return map.containsKey(key) && get(key) != null;
    }
}
