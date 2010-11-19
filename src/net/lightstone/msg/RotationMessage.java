package net.lightstone.msg;

public final class RotationMessage extends Message {

	private final float rotation, pitch;
	private final boolean flying;

	public RotationMessage(float rotation, float pitch, boolean flying) {
		this.rotation = rotation;
		this.pitch = pitch;
		this.flying = flying;
	}

	public float getRotation() {
		return rotation;
	}

	public float getPitch() {
		return pitch;
	}

	public boolean isFlying() {
		return flying;
	}

}
