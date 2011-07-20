package net.glowstone.io;

/**
 * An operation for the storage queue
 */
public abstract class StorageOperation implements Runnable {

    /**
     * Can this operation run in parallel with other operation types
     */
    public abstract boolean isParallel();

    /**
     * The group for this operation (world name for example)
     */
    public abstract String getGroup();

    /**
     * Whether multiple of this operation can be in the storage queue at once
     * @return
     */
    public abstract boolean queueMultiple();

    /**
     * The name of the operation being performed.
     * @return
     */
    public abstract String getOperation();

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof StorageOperation))
            return false;
        StorageOperation op = (StorageOperation) other;
        return getGroup().equals(op.getGroup())
                && getOperation().equals(op.getOperation())
                && isParallel() == op.isParallel()
                && queueMultiple() == op.queueMultiple();
    }
}
