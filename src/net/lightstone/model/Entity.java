package net.lightstone.model;

import net.lightstone.world.World;

public abstract class Entity {

	protected final World world;

	protected final int id;

	protected Position position = Position.ZERO;

	protected Rotation rotation = Rotation.ZERO;

	public Entity(World world) {
		this.world = world;
		this.id = world.getEntities().allocate(this);
	}

	public World getWorld() {
		return world;
	}

	public void destroy() {
		world.getEntities().deallocate(this);
	}

	public int getId() {
		return id;
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
