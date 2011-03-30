package net.glowstone.msg;

public final class EntityEquipmentMessage extends Message {

	private final int id, slot, item, damage;

	public EntityEquipmentMessage(int id, int slot, int item, int damage) {
		this.id = id;
		this.slot = slot;
		this.item = item;
		this.damage = damage;
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

	public int getDamage() {
		return damage;
	}

}
