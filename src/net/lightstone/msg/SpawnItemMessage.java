package net.lightstone.msg;

import net.lightstone.model.Item;

public final class SpawnItemMessage extends Message {

	private final int id, x, y, z, rotation, pitch, roll;
    private final Item item;

	public SpawnItemMessage(int id, Item item, int x, int y, int z, int rotation, int pitch, int roll) {
		this.id = id;
		this.item = item;
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

	public Item getItem() {
		return item;
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
