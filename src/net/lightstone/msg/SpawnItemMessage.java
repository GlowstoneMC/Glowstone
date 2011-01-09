package net.lightstone.msg;

public final class SpawnItemMessage extends Message {

	private final int id, item, count, x, y, z, rotation, pitch, roll;

	public SpawnItemMessage(int id, int item, int count, int x, int y, int z, int rotation, int pitch, int roll) {
		this.id = id;
		this.item = item;
		this.count = count;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotation = rotation;
		this.pitch = pitch;
		this.roll = roll;
	}

	public int getId() {
		return id;
	}

	public int getItem() {
		return item;
	}

	public int getCount() {
		return count;
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

	public int getRoll() {
		return roll;
	}

}
