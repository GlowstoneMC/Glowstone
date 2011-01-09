package net.lightstone.msg;

public final class SetWindowSlotMessage extends Message {

	private final int id, slot, item, count, uses;

	public SetWindowSlotMessage(int id, int slot) {
		this(id, slot, -1, 0, 0);
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

	public int getCount() {
		return count;
	}

	public int getUses() {
		return uses;
	}

	public SetWindowSlotMessage(int id, int slot, int item, int count, int uses) {
		this.id = id;
		this.slot = slot;
		this.item = item;
		this.count = count;
		this.uses = uses;
	}

}
