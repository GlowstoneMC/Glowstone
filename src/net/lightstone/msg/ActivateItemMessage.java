package net.lightstone.msg;

public final class ActivateItemMessage extends Message {

	private final int id, item;

	public ActivateItemMessage(int id, int item) {
		this.id = id;
		this.item = item;
	}

	public int getId() {
		return id;
	}

	public int getItem() {
		return item;
	}

}
