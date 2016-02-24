package net.glowstone.util.collection;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Set which delegates to other sets.
 */
public class SuperSet<E> extends SuperCollection<E> implements Set<E> {

    public SuperSet() {
        super(SuperCollection.AdditionMode.ALL);
    }

    public SuperSet(List<Set<E>> parents) {
        super(parents, SuperCollection.AdditionMode.ALL);
    }

    @Override
    public List<Set<E>> getParents() {
        return (List<Set<E>>) super.getParents();
    }

    @Override
    public Set<E> asClone() {
        Set<E> output = new HashSet<>();

        getParents().forEach(output::addAll);

        return output;
    }

    @Override
    protected Class<? extends Collection> getCollectionClass() {
        return Set.class;
    }
}
