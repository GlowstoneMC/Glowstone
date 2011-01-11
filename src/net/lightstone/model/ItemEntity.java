package net.lightstone.model;

import net.lightstone.world.World;

public final class ItemEntity extends Entity {

	private final Item item;

	public ItemEntity(World world, Item item) {
		super(world);
		this.item = item;
	}

	public Item getItem() {
		return item;
	}

}
