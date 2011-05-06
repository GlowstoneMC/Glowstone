package net.glowstone.inventory;

/**
 * An immutable class which represents an in-game item (this also includes
 * blocks which are dropped on the floor or placed in the player's inventory).
 * @author Graham Edgecombe
 */
public class ConstItemStack {

    /**
     * The item's id.
     */
	private final int id;

    /**
     * The number of items within the stack.
     */
    private final int count;

    /**
     * The item's damage.
     */
    private final int damage;

    /**
     * Creates a single item with no damage.
     * @param id The item id.
     */
	public ConstItemStack(int id) {
		this(id, 1);
	}

    /**
     * Creates an item with no damage.
     * @param id The id.
     * @param count The number of items within the stack.
     */
	public ConstItemStack(int id, int count) {
		this(id, count, 0);
	}

    /**
     * Creates an item with the specified count and damage. Generally items that
     * can be damaged cannot be stacked so the count should be one.
     * @param id The id.
     * @param count The number of items within the stack.
     * @param damage The damage.
     */
	public ConstItemStack(int id, int count, int damage) {
		this.id = id;
		this.count = count;
		this.damage = damage;
	}

    /**
     * Gets the id of this item.
     * @return The id.
     */
	public int getId() {
		return id;
	}

    /**
     * Gets the number of items on a stack, generally this is between 1 and 64.
     * @return The count of this item.
     */
	public int getCount() {
		return count;
	}

    /**
     * Gets the damage of this item.
     * @return The damage of this item.
     */
	public int getDamage() {
		return damage;
	}

}
