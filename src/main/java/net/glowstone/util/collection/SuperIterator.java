package net.glowstone.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An Iterator which delegates in other iterators.
 */
public class SuperIterator<E> implements Iterator<E> {

    private final Iterator<Iterable<E>> parentIterator;
    private Iterator<E> childIterator;

    public SuperIterator(Iterable<Iterable<E>> iterable) {
        this(iterable.iterator());
    }

    public SuperIterator(Iterator<Iterable<E>> parentIterator) {
        this.parentIterator = parentIterator;
    }

    @Override
    public boolean hasNext() {
        while (childIterator == null || !childIterator.hasNext()) {
            if (parentIterator.hasNext()) {
                childIterator = parentIterator.next().iterator();
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
