package net.glowstone.util.collection;

import net.glowstone.util.Adapter;

import java.util.Iterator;

/**
 * Converts an Iterable to an Iterator by calling method "iterator".
 */
public class IterableIteratorAdapter implements Adapter<Iterable, Iterator> {

    @Override
    public Iterator adapt(Iterable iterable) {
        return iterable.iterator();
    }

    public static final IterableIteratorAdapter INSTANCE = new IterableIteratorAdapter();
}
