package net.glowstone.msg;

public final class EntityActionMessage extends Message {

	private final int id, action;

	public EntityActionMessage(int id, int action) {
		this.id = id;
		this.action = action;
	}

	public int getId() {
		return id;
	}

	public int getAction() {
		return action;
	}

}
