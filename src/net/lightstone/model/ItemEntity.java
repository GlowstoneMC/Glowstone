package net.lightstone.model;

public final class ItemEntity extends Entity {

	private final Item item;

	public ItemEntity(Item item) {
		this.item = item;
	}

	public Item getItem() {
		return item;
	}

}
