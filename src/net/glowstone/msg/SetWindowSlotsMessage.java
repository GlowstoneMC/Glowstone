package net.glowstone.msg;

import net.glowstone.model.Item;

public final class SetWindowSlotsMessage extends Message {

	private final int id;
	private final Item[] items;

	public SetWindowSlotsMessage(int id, Item[] items) {
		this.id = id;
		this.items = items;
	}

	public int getId() {
		return id;
	}

	public Item[] getItems() {
		return items;
	}

}
