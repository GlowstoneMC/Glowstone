package net.lightstone.msg;

public final class BlockPlacementMessage extends Message {

	private final int id, x, y, z, direction;

	public BlockPlacementMessage(int id, int x, int y, int z, int direction) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = direction;
	}

	public int getId() {
		return id;
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

	public int getDirection() {
		return direction;
	}

}
