package net.glowstone.util.collection;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An Iterator which delegates in other iterators.
 */
public class SuperIterator<E> implements Iterator<E> {

    private final Iterator<Iterable<E>> parentIterator;
    private Iterator<E> childIterator;

    public SuperIterator(Iterable<Iterable<E>> iterable) {
        this(iterable.iterator());
    }

    public SuperIterator(List<List<E>> nestedList) {
        this(nestedList.stream()
                .map((Function<List<E>, Iterable<E>>) e -> e)
                .collect(Collectors.toList())
                .iterator()
        );
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
