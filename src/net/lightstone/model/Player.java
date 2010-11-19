package net.lightstone.model;

public final class Player extends Mob {

	private final String name;

	public Player(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
