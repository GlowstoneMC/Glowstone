package net.lightstone.msg;

public final class EntityRotationMessage extends Message {

	private final int id, rotation, pitch;

	public EntityRotationMessage(int id, int rotation, int pitch) {
		this.id = id;
		this.rotation = rotation;
		this.pitch = pitch;
	}

	public int getId() {
		return id;
	}

	public int getRotation() {
		return rotation;
	}

	public int getPitch() {
		return pitch;
	}

}
