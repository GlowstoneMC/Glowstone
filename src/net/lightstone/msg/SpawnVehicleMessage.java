package net.lightstone.msg;

public final class SpawnVehicleMessage extends Message {

	private final int id, type, x, y, z;

	public SpawnVehicleMessage(int id, int type, int x, int y, int z) {
		this.id = id;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
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

}
