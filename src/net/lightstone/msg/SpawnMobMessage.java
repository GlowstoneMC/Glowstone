package net.lightstone.msg;

public class SpawnMobMessage extends Message {

	private final int id, type, x, y, z, rotation, pitch;

	public SpawnMobMessage(int id, int type, int x, int y, int z, int rotation, int pitch) {
		this.id = id;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotation = rotation;
		this.pitch = pitch;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
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

	public int getRotation() {
		return rotation;
	}

	public int getPitch() {
		return pitch;
	}

}
