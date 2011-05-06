package net.glowstone.entity;

import net.glowstone.inventory.ItemStack;
import net.glowstone.util.Position;
import net.glowstone.msg.Message;
import net.glowstone.msg.SpawnItemMessage;
import net.glowstone.World;

/**
 * Represents an item that is also an {@link Entity} within the world.
 * @author Graham Edgecombe
 */
public final class ItemEntity extends Entity {

	/**
	 * The item.
	 */
	private final ItemStack item;

	/**
	 * Creates a new item entity.
	 * @param world The world.
	 * @param item The item.
	 */
	public ItemEntity(World world, ItemStack item) {
		super(world);
		this.item = item;
	}

	/**
	 * Gets the item that this {@link ItemEntity} represents.
	 * @return The item.
	 */
	public ItemStack getItem() {
		return item;
	}

	@Override
	public Message createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

		return new SpawnItemMessage(id, item, x, y, z, yaw, pitch, 0);
	}

	@Override
	public Message createUpdateMessage() {
		// TODO we can probably use some generic implementation for all of
		// these
		return null;
	}

}
