package net.glowstone.msg;

public final class DestroyEntityMessage extends Message {

	private final int id;

	public DestroyEntityMessage(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
