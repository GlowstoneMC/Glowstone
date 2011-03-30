package net.glowstone.msg;

public final class UpdateSignMessage extends Message {

	private final int x, y, z;
	private final String[] message;

	public UpdateSignMessage(int x, int y, int z, String[] message) {
		if (message.length != 4) {
			throw new IllegalArgumentException();
		}

		this.x = x;
		this.y = y;
		this.z = z;
		this.message = message;
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

	public String[] getMessage() {
		return message;
	}

}
