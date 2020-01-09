package net.glowstone.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * List which delegates to other lists.
 */
public class SuperList<E> extends SuperCollection<E> implements List<E> {

    public SuperList() {
        super(AdditionMode.LAST);
    }

    public SuperList(List<List<E>> parents) {
        super(parents, AdditionMode.LAST);
    }

    @Override
    public List<List<E>> getParents() {
        return (List<List<E>>) super.getParents();
    }

    @Override
    public List<E> asClone() {
        List<E> output = new ArrayList<>();

        getParents().forEach(output::addAll);

        return output;
    }

    @Override
    protected Class<? extends Collection> getCollectionClass() {
        return List.class;
    }

    @Override
    public void add(int index, E object) {
        int relativePos = index;

        for (List<E> parent : getParents()) {
            int parentSize = parent.size();

            if (parentSize <= relativePos) {
                parent.add(relativePos, object);
                return;
            }

            relativePos -= parentSize;
        }

        throw new IndexOutOfBoundsException("This list does not contain position " + index);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> objects) {
        int relativePos = index;
        int modified = 0;

        for (List<E> parent : getParents()) {
            int parentSize = parent.size();

            if (parentSize <= relativePos) {
                return parent.addAll(relativePos, objects);
            }

            relativePos -= parentSize;
        }

        throw new IndexOutOfBoundsException("This list does not contain position " + index);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (object == this) {
            return true;
        }

        if (!(object instanceof List)) {
            return false;
        }

        List<?> other = (List<?>) object;

        if (other.size() != size()) {
            return false;
        }

        Iterator<E> thisIterator = iterator();
        Iterator<?> otherIterator = other.iterator();
        while (thisIterator.hasNext()) {
            if (!thisIterator.next().equals(otherIterator.next())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public E get(int index) {
        int relativePos = index;

        for (List<E> parent : getParents()) {
            int parentSize = parent.size();

            if (relativePos < parentSize) {
                return parent.get(relativePos);
            }

            relativePos -= parentSize;
        }

        throw new IndexOutOfBoundsException("This list does not contain position " + index);
    }

    @Override
    public Iterator<E> iterator() {
        return new SuperIterator<>(getParents());
    }

    @Override
    public E remove(int index) {
        int relativePos = index;

        for (List<E> parent : getParents()) {
            int parentSize = parent.size();

            if (relativePos < parentSize) {
                return parent.remove(relativePos);
            }

            relativePos -= parentSize;
        }

        throw new IndexOutOfBoundsException("This list does not contain position " + index);
    }

    @Override
    public int indexOf(Object object) {
        int relativePos = 0;

        for (List<E> parent : getParents()) {
            int parentIndex = parent.indexOf(object);

            if (parentIndex >= 0) {
                return relativePos + parentIndex;
            }

            relativePos += parent.size();
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object object) {
        List<List<E>> parents = getParents();
        ListIterator<List<E>> iterator = parents.listIterator(parents.size());
        int index = -1;

        while (iterator.hasPrevious()) {
            List<E> parent = iterator.previous();

            if (index < 0) {
                index = parent.lastIndexOf(object);
            } else {
                index += parent.size();
            }
        }

        return index;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new SuperListIterator<>(getParents());
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new SuperListIterator<>(getParents(), index);
    }

    @Override
    public E set(int index, E object) {
        int relativePos = index;

        for (List<E> parent : getParents()) {
            int parentSize = parent.size();

            if (relativePos < parentSize) {
                return parent.set(relativePos, object);
            }

            relativePos -= parentSize;
        }

        throw new IndexOutOfBoundsException("This list does not contain position " + index);
    }

    @Override
    public int size() {
        int size = 0;

        for (List<E> parent : getParents()) {
            size += parent.size();
        }

        return size;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        // Kinda slow. If this is ever going to be used heavily, you'll probably want to implement a
        // "SubList" class, since neither Java nor Guava provides a public implementation.
        return asClone().subList(fromIndex, toIndex);
    }
}
