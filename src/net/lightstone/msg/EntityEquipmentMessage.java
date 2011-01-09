package net.lightstone.msg;

public final class EntityEquipmentMessage extends Message {

	private final int id, slot, item;

	public EntityEquipmentMessage(int id, int slot, int item) {
		this.id = id;
		this.slot = slot;
		this.item = item;
	}

	public int getId() {
		return id;
	}

	public int getSlot() {
		return slot;
	}

	public int getItem() {
		return item;
	}

}
