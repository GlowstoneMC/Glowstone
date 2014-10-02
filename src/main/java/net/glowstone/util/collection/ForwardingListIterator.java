package net.glowstone.util.collection;

import java.util.ListIterator;

/**
 * Generic forwarding list iterator with optional immutability override.
 */
public class ForwardingListIterator<E> extends com.google.common.collect.ForwardingListIterator<E> {

    private ListIterator<E> parent;
    private boolean forceImmutable;

    public ForwardingListIterator(ListIterator<E> parent) {
        this(parent, false);
    }

    public ForwardingListIterator(ListIterator<E> parent, boolean forceImmutable) {
        this.parent = parent;
        this.forceImmutable = forceImmutable;
    }

    private void checkMutable() {
        if (forceImmutable) {
            throw new UnsupportedOperationException("This iterator does not support addition");
        }
    }

    @Override
    public void add(E object) {
        checkMutable();
        super.add(object);
    }    

    @Override
    protected ListIterator<E> delegate() {
        return parent;
    }

    @Override
    public void remove() {
        checkMutable();
        super.remove();
    }    
}
