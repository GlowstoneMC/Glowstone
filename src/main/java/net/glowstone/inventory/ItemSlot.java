package net.glowstone.inventory;

/**
 * A class which represents an {@link ItemStack} and its associated slot in an
 * inventory.
 */
public final class ItemSlot {

    /**
     * The slot.
     */
	private final int slot;

    /**
     * The item.
     */
	private final ItemStack item;

    /**
     * Creates a slotted item.
     * @param slot The slot.
     * @param item The item.
     */
	public ItemSlot(int slot, ItemStack item) {
		this.slot = slot;
		this.item = item;
	}

    /**
     * Gets the slot.
     * @return The slot.
     */
	public int getSlot() {
		return slot;
	}

    /**
     * Gets the item.
     * @return The item.
     */
	public ItemStack getItem() {
		return item;
	}

}
