package net.lightstone.msg;

public final class WindowClickMessage extends Message {

	private final int id, slot;
	private final boolean rightClick;
	private final int transaction, item, count, damage;

	public WindowClickMessage(int id, int slot, boolean rightClick, int transaction) {
		this(id, slot, rightClick, transaction, -1, 0, 0);
	}

	public WindowClickMessage(int id, int slot, boolean rightClick, int transaction, int item, int count, int damage) {
		this.id = id;
		this.slot = slot;
		this.rightClick = rightClick;
		this.transaction = transaction;
		this.item = item;
		this.count = count;
		this.damage = damage;
	}

	public int getId() {
		return id;
	}

	public int getSlot() {
		return slot;
	}

	public boolean isRightClick() {
		return rightClick;
	}

	public int getTransaction() {
		return transaction;
	}

	public int getItem() {
		return item;
	}

	public int getCount() {
		return count;
	}

	public int getDamage() {
		return damage;
	}

}
