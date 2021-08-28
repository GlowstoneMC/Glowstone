package net.glowstone.util.collection;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Generic super collection. This is an abstract collection which delegates (ie redirects
 * operations) to other collections (its children). This class is employed to reduce the overhead of
 * copying objects from several collections to create a new larger one.
 *
 * <p>This class is a generic collection which serves as a base for other super collections. It
 * handles non-indexed accesses to collections like additions, removals, contains...
 *
 * <p>Note that because this collection holds references to other collections, modifications to
 * children will be reflected here. Also, modifications to this collection will affect its
 * children.
 *
 * <p>If you need a collection that has the very same contents but isn't affected by operations to
 * children, you can employ the {@link #asClone()} method, which returns a new collection with the
 * same contents.
 *
 * <p>Since there are several children and not all may return the same return value for certain
 * operations, you can control how this class behaves by means of the {@link
 * #setResultMode(ResultMode)} method. It defaults to ANY, so operations that return booleans will
 * return true as long as at least one children succeeded.
 */
public abstract class SuperCollection<E> implements Collection<E> {

    private final List<? extends Collection<E>> parents;
    /**
     * Current result mode.
     *
     * <p>If mode is set to ANY, operations will return "true" as long as the parents returned
     * "true" at least once.
     *
     * <p>If mode is set to ALL, operations will only return "true" if all parents also returned
     * "true".
     *
     * @param resultMode Result mode.
     * @return Result mode.
     */
    @Getter
    @Setter
    private ResultMode resultMode = ResultMode.ANY;
    /**
     * Determines how this collection will behave to additions.
     *
     * <p>If mode is set to ALL, the addition will be performed on every parent. Default for sets.
     *
     * <p>If mode is set to LAST, the operation will be performed on the last parent only. Default
     * for lists.
     *
     * @param additionMode Addition mode.
     * @return Addition mode.
     */
    @Getter
    @Setter
    private AdditionMode additionMode;

    public SuperCollection(AdditionMode additionMode) {
        this(new ArrayList<>(), additionMode);
    }

    public SuperCollection(List<? extends Collection<E>> parents, AdditionMode additionMode) {
        this.parents = parents;
        this.additionMode = additionMode;
    }

    /**
     * Returns the list of parents.
     *
     * @return Parent list.
     */
    public List<? extends Collection<E>> getParents() {
        // TODO: Replace with facade
        return parents;
    }

    /**
     * Returns a new collection with the same contents as the parents.
     *
     * @return New mutable collection.
     */
    public abstract Collection<E> asClone();

    /**
     * Returns the class this SuperCollection implements.
     *
     * @return Collection class.
     */
    protected abstract Class<? extends Collection> getCollectionClass();

    protected boolean resultBoolean(int modified) {
        switch (resultMode) {
            case ANY:
                return modified > 0;

            case ALL:
                return modified >= parents.size();
            default:
                return false;
        }
    }

    @Override
    public boolean add(E object) {
        switch (additionMode) {

            case ALL:
                int modified = 0;
                for (Collection<E> parent : parents) {
                    if (parent.add(object)) {
                        modified++;
                    }
                }

                return resultBoolean(modified);

            case LAST:
                return parents.get(parents.size() - 1).add(object);
            default:
                throw new IllegalStateException(
                    "This SuperCollection has an invalid addition mode!");
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> objects) {
        switch (additionMode) {

            case ALL:
                int modified = 0;

                for (Collection<E> parent : parents) {
                    if (parent.addAll(objects)) {
                        modified++;
                    }
                }

                return resultBoolean(modified);

            case LAST:
                return parents.get(parents.size() - 1).addAll(objects);
            default:
                throw new IllegalStateException(
                    "This SuperCollection has an invalid addition mode!");
        }
    }

    @Override
    public void clear() {
        parents.forEach(Collection::clear);
    }

    @Override
    public boolean contains(Object object) {
        for (Collection<E> parent : parents) {
            if (parent.contains(object)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        for (Object object : objects) {
            if (!contains(object)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object object) {
        // Avoid cloning if possible
        if (object == null) {
            return false;
        }

        if (object == this) {
            return true;
        }

        if (!getCollectionClass().isInstance(object)) {
            return false;
        }

        // If trivial comparisons didn't work, fall back to clone (to remove duplicates) and compare
        return asClone().equals(object);
    }

    @Override
    public int hashCode() {
        int code = 0;

        for (Collection<E> parent : parents) {
            code += parent.hashCode();
        }

        return code;
    }

    @Override
    public boolean isEmpty() {
        for (Collection<E> parent : parents) {
            if (!parent.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Iterator<E> iterator() {
        // Override if possible, because this is *really* slow
        return asClone().iterator();
    }

    @Override
    public boolean remove(Object object) {
        int modified = 0;

        for (Collection<E> parent : parents) {
            if (parent.remove(object)) {
                modified++;
            }
        }

        return resultBoolean(modified);
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        int modified = 0;

        for (Collection<E> parent : parents) {
            if (parent.removeAll(objects)) {
                modified++;
            }
        }

        return resultBoolean(modified);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        int modified = 0;

        for (Collection<E> parent : parents) {
            if (parent.retainAll(objects)) {
                modified++;
            }
        }

        return resultBoolean(modified);
    }

    @Override
    public int size() {
        // Override if possible
        return asClone().size();
    }

    @Override
    public Object[] toArray() {
        // Override if possible
        return asClone().toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        // Override if possible
        return asClone().toArray(array);
    }

    @Override
    public String toString() {
        // This is as other methods that call the asClone method, but since it's only used for
        // eventual debugging, its performance doesn't really matter, so don't lose your time
        // overriding and implementing it in subclasses.
        return asClone().toString();
    }

    public enum ResultMode {
        NEVER,
        ALL,
        ANY
    }

    public enum AdditionMode {
        ALL,
        LAST
    }
}
