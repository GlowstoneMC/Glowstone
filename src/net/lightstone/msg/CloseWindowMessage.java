package net.lightstone.msg;

public final class CloseWindowMessage extends Message {

	private final int id;

	public CloseWindowMessage(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
