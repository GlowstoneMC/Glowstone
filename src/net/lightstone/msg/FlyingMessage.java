package net.lightstone.msg;

public final class FlyingMessage extends Message {

	private final boolean visible;

	public FlyingMessage(boolean flying) {
		this.visible = flying;
	}

	public boolean isFlying() {
		return visible;
	}

}
