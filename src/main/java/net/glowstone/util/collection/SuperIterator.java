package net.glowstone.util.collection;

import net.glowstone.util.Adapter;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An Iterator which delegates in other iterators.
 */
public class SuperIterator<E> implements Iterator<E> {
    private final Iterator parentIterator;
    private Iterator<E> childIterator;
    private Adapter adapter;

    public SuperIterator(Iterable<Iterable<E>> iterable) {
        this(iterable.iterator());
    }

    public SuperIterator(Iterator<Iterable<E>> parentIterator) {
        this(parentIterator, IterableIteratorAdapter.INSTANCE);
    }

    public SuperIterator(Iterable iterable, Adapter<?, Iterator> adapter) {
        this(iterable.iterator(), adapter);
    }

    public SuperIterator(Iterator parentIterator, Adapter<?, Iterator> adapter) {
        this.parentIterator = parentIterator;
        this.adapter = adapter;
    }

    @Override
    public boolean hasNext() {
        while (childIterator == null || !childIterator.hasNext()) {
            if (parentIterator.hasNext()) {
                childIterator = (Iterator<E>) adapter.adapt(parentIterator.next());
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        return childIterator.next();
    }

    @Override
    public void remove() {
        if (childIterator == null) {
            throw new IllegalStateException("next() must be called before using remove()");
        }

        childIterator.remove();
    }
}
