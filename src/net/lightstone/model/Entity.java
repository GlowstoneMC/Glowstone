package net.lightstone.model;

public abstract class Entity {

	protected final int id;

	protected Position position;

	protected Rotation rotation;

	public Entity() {
		this.id = 0; // TODO allocate the ID
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Rotation getRotation() {
		return rotation;
	}

	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
	}

}
