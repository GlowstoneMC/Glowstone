package net.lightstone.model;

public abstract class Mob extends Entity {

	protected double rotation, pitch;

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public double getPitch() {
		return pitch;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

}
