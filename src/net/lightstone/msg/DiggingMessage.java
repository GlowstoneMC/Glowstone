package net.lightstone.msg;

public final class DiggingMessage extends Message {

	private final int state, x, y, z, face;

	public DiggingMessage(int state, int x, int y, int z, int face) {
		this.state = state;
		this.x = x;
		this.y = y;
		this.z = z;
		this.face = face;
	}

	public int getState() {
		return state;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getFace() {
		return face;
	}

}
