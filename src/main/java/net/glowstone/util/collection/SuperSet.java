package net.glowstone.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Set which delegates to other sets.
 */
public class SuperSet<E> implements Set<E> {
    private final List<Set<E>> parents;
    private ResultMode resultMode = ResultMode.ANY;

    public SuperSet() {
        this(new ArrayList<Set<E>>());
    }

    public SuperSet(List<Set<E>> parents) {
        this.parents = parents;
    }

    /**
     * Returns the list of parents.
     * @return Parent list.
     */
    public List<Set<E>> getParents() {
        return parents;
    }

    /**
     * Sets what will this set return for operations.
     * If mode is set to ANY, operations will return "true" as long as the parents returned "true" at least once.
     * If mode is set to ALL, operations will only return "true" if all parents also returned "true".
     */
    public void setResultMode(ResultMode resultMode) {
        this.resultMode = resultMode;
    }

    /**
     * Returns current result mode.
     * @return Result mode.
     */
    public ResultMode getResultMode() {
        return resultMode;
    }

    /**
     * Returns this SuperSet as a new Set which is not linked to any other set.
     * @return New mutable set.
     */
    public Set<E> asClonedSet() {
        Set<E> output = new HashSet<>();

        for (Set<E> parent : parents) {
            output.addAll(parent);
        }

        return output;
    }

    private boolean resultBoolean(int modified) {
        switch (resultMode) {
            case ANY:
                return modified > 0;

            case ALL:
                return modified >= parents.size();
        }

        return false;
    }
    
    @Override
    public boolean add(E object) {
        int modified = 0;

        for (Set<E> parent : parents) {
            if (parent.add(object)) {
                modified++;
            }
        }

        return resultBoolean(modified);
    }

    @Override
    public boolean addAll(Collection<? extends E> objects) {
        int modified = 0;

        for (Set<E> parent : parents) {
            if (parent.addAll(objects)) {
                modified++;
            }
        }

        return resultBoolean(modified);
    }

    @Override
    public void clear() {
        for (Set<E> parent : parents) {
            parent.clear();
        }
    }

    @Override
    public boolean contains(Object object) {
        for (Set<E> parent : parents) {
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
    public boolean equals(Object obj) {
        // Avoid cloning if possible
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Set)) {
            return false;
        }

        // If trivial comparisons didn't work, fall back to clone (to remove duplicates) and compare
        return asClonedSet().equals(obj);
    }

    @Override
    public int hashCode() {
        int code = 0;

        for (Set<E> parent : parents) {
            code += parent.hashCode();
        }

        return code;
    }

    @Override
    public boolean isEmpty() {
        for (Set<E> parent : parents) {
            if (!parent.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Iterator<E> iterator() {
        // Ugly and inefficient, but I can't think any other way to do this.
        return asClonedSet().iterator();
    }

    @Override
    public boolean remove(Object object) {
        int modified = 0;

        for (Set<E> parent : parents) {
            if (parent.remove(object)) {
                modified++;
            }
        }

        return resultBoolean(modified);
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        int modified = 0;

        for (Set<E> parent : parents) {
            if (parent.removeAll(objects)) {
                modified++;
            }
        }

        return resultBoolean(modified);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        int modified = 0;

        for (Set<E> parent : parents) {
            if (parent.retainAll(objects)) {
                modified++;
            }
        }

        return resultBoolean(modified);
    }

    @Override
    public int size() {
        // Has to be cloned to a new set to avoid duplicated. Also slow.
        return asClonedSet().size();
    }

    @Override
    public Object[] toArray() {
        // Cloned to avoid duplicates. Avoid!
        return asClonedSet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        // Also slow. Avoid if possible.
        return asClonedSet().<T>toArray(array);
    }

    public static enum ResultMode {
        NEVER,
        ALL,
        ANY;
    }
}
