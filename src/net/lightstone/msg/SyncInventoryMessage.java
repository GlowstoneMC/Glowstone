package net.lightstone.msg;

import net.lightstone.model.Item;

public final class SyncInventoryMessage extends Message {

	private final int type;
	private final Item[] items;

	public SyncInventoryMessage(int type, Item[] items) {
		this.type = type;
		this.items = items;
	}

	public int getType() {
		return type;
	}

	public Item[] getItems() {
		return items;
	}

	public int getSlots() {
		return items.length;
	}

}
