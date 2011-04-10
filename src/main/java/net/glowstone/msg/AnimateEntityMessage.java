package net.glowstone.msg;

public final class AnimateEntityMessage extends Message {

	private final int id, animation;

	public AnimateEntityMessage(int id, int animation) {
		this.id = id;
		this.animation = animation;
	}

	public int getId() {
		return id;
	}

	public int getAnimation() {
		return animation;
	}

}
