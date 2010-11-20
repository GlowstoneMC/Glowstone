package net.lightstone.model;

public class Item {

	private final int id, count, health;

	public Item(int id) {
		this(id, 1);
	}

	public Item(int id, int count) {
		this(id, count, 0);
	}

	public Item(int id, int count, int health) {
		this.id = id;
		this.count = count;
		this.health = health;
	}

	public int getId() {
		return id;
	}

	public int getCount() {
		return count;
	}

	public int getHealth() {
		return health;
	}

}
