package net.glowstone.block.itemtype;

/**
 * Base class for specific types of items.
 */
public class ItemType {

    /**
     * The maximum stack size of the item.
     */
    private int maxStackSize = 64;

    ////////////////////////////////////////////////////////////////////////////
    // Setters for subclass use

    /**
     * Set the maximum stack size of the item.
     * @param maxStackSize The new maximum stack size.
     */
    protected void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Public accessors

    /**
     * Get the maximum stack size of the item.
     * @return The maximum stack size.
     */
    public int getMaxStackSize() {
        return maxStackSize;
    }
}
