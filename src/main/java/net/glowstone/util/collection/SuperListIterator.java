package net.glowstone.util.collection;

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An Iterator which delegates in other iterators.
 */
public class SuperListIterator<E> implements ListIterator<E> {

    private final ListIterator<List<E>> parentIterator;
    private ListIterator<E> childIterator;
    private int index;

    public SuperListIterator(List<List<E>> parents) {
        this(parents.listIterator());
    }

    public SuperListIterator(ListIterator<List<E>> parentIterator) {
        this.parentIterator = parentIterator;
    }

    /**
     * Creates an instance.
     *
     * @param parents a list of lists to iterate in a flattened manner
     * @param index   the initial index
     */
    public SuperListIterator(List<List<E>> parents, int index) {
        this(parents);
        this.index = index;

        while (parentIterator.hasNext()) {
            List<E> child = parentIterator.next();
            int childSize = child.size();

            if (index <= childSize) {
                childIterator = child.listIterator(index);
                return;
            }

            index -= childSize;
        }

        throw new IndexOutOfBoundsException();
    }

    @Override
    public boolean hasNext() {
        while (childIterator == null || !childIterator.hasNext()) {
            if (parentIterator.hasNext()) {
                childIterator = parentIterator.next().listIterator();
            } else {
                return false;
            }
        }

        return true;
    }

    private void checkNext() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public int nextIndex() {
        checkNext();
        return index + 1;
    }

    @Override
    public E next() {
        checkNext();
        E obj = childIterator.next();
        index++;
        return obj;
    }

    @Override
    public boolean hasPrevious() {
        while (childIterator == null || !childIterator.hasPrevious()) {
            if (parentIterator.hasPrevious()) {
                List<E> child = parentIterator.previous();
                childIterator = child.listIterator(child.size());
            } else {
                return false;
            }
        }

        return true;
    }

    private void checkPrevious() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public int previousIndex() {
        checkPrevious();
        return index - 1;
    }

    @Override
    public E previous() {
        checkPrevious();
        E obj = childIterator.previous();
        index--;
        return obj;
    }

    @Override
    public void add(E object) {
        if (childIterator == null) {
            throw new IllegalStateException("next() must be called before using add()");
        }

        childIterator.add(object);
        index++;
    }

    @Override
    public void remove() {
        if (childIterator == null) {
            throw new IllegalStateException("next() must be called before using remove()");
        }

        childIterator.remove();
    }

    @Override
    public void set(E object) {
        if (childIterator == null) {
            throw new IllegalStateException("next() must be called before using set(E)");
        }

        childIterator.set(object);
    }
}
